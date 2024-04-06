#version 330
#define PI 3.1415926538

in vec2 inPosition;

uniform mat4 uView;
uniform mat4 uProj;
uniform int uFunction;
uniform float uTime;
uniform mat4 uModel;
uniform float uMorph;

uniform sampler2D texture1;
uniform sampler2D texture2;

uniform vec3 color1;
uniform vec3 color2;

out vec3 fragPos;
out vec3 Normal;
out vec2 TexCoords;
out vec4 outColor;

// Sfericke
vec3 graphulus(vec2 inPos) {
    inPos.x = mod(inPos.x, 1.0);
    inPos.y = mod(inPos.y, 1.0);
    float theta = inPos.x * 2.0 * PI;
    float phi = inPos.y * PI;
    float radius = 3.0 + cos(phi);
    float x = cos(theta) * radius;
    float y = sin(theta) * radius;
    float z = sin(phi);

    return vec3(x, y, z);
}

// Kartezske
vec3 valec(vec2 inPos) {
    inPos.x *= 6.3f;
    inPos.y *= 6.3f;
    float r = 3.0 + sin(uTime);
    float x = cos(inPos.x) * r;
    float y = sin(inPos.x) * r;
    float z = inPos.y;
    return vec3(x, y, z);
}

//Cylindricke
vec3 unknown(vec2 inPos) {
    inPos.x = mod(inPos.x, 1.0);
    inPos.y = mod(inPos.y, 1.0);
    float azimut = inPos.x * 2.0 * PI;
    float v = inPos.y * PI;
    float r = 3.0 * cos(4.0 * v);
    float x = cos(azimut) * r;
    float y = sin(azimut) * r;
    float z = sin(v);
    return vec3(x, y, z);
}

//sfericke
vec3 gula(vec2 inPos) {
    inPos.x *= 6.3f;
    inPos.y *= 6.3f;
    float az = inPos.x * PI;
    float ze = inPos.y * PI / 2;
    float r = 0.5;

    float x = r * cos(az) * cos(ze);
    float y = r * sin(az) * cos(ze);
    float z = r * sin(ze);

    return vec3(x, y, z);
}

vec3 kuzel(vec2 inPos) {
    inPos.x = mod(inPos.x, 1.0);
    inPos.y = mod(inPos.y, 1.0);
    float r = 1.0;
    float h = 5.0;
    float numTurns = 5.0;
    float theta = inPos.x * 2.0 * PI;
    float z = inPos.y * h;
    r *= 1.0 - inPos.y;
    float x = r * cos(theta);
    float y = r * sin(theta);
    return vec3(x, y, z);
}

//cylindricke
vec3 sombrero(vec2 inPos) {
    float r = inPos.x * 2.f * PI;
    float azimut = inPos.y * 2.f * PI;
    float v = 2.f * sin(r);
    float x = cos(azimut) * r;
    float y = sin(azimut) * r;
    float z = v;
    return vec3(x, y, z);
}

vec3 posCalc(vec2 inPosition) {
    vec3 pos1, pos2;
    switch (uFunction) {
        case 0: pos1 = graphulus(inPosition);
                pos2 = valec(inPosition);
                return mix(pos1, pos2, uMorph);
        case 2: return graphulus(inPosition);
        case 3: return valec(inPosition);
        case 4: return unknown(inPosition);
        case 5: return gula(inPosition);
        case 6: return kuzel(inPosition);
        case 7: return sombrero(inPosition);
        default: return vec3(inPosition, 0.f);
    }
}

void main() {
    vec2 newPos = inPosition * 2 - 1;
    float x, y, z;

    vec4 objectPosition;
    objectPosition = uView * vec4(posCalc(inPosition), 1.f);
    TexCoords = inPosition;
    gl_Position = uProj * objectPosition;

    vec4 textureColor = mix(texture(texture1, TexCoords), texture(texture2, TexCoords), uMorph);

    vec3 finalColor = mix(color1, color2, uMorph) * textureColor.rgb;

    outColor = vec4(finalColor, textureColor.a);

  /*  fragPos = vec3(x, y, z);
    TexCoords = inPosition;

    gl_Position = uProj * uView * vec4(fragPos, 1.0);*/
}
