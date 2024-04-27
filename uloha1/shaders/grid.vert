#version 330
#define PI 3.1415926538
in vec2 inPosition;


uniform mat4 uView;
uniform mat4 uProj;
uniform float uFunction;
uniform float uTime;
uniform mat4 uModel;
uniform float uMorph;


uniform vec3 lightPosition;
uniform vec3 eyePosition;

uniform  float spotCutOff;

uniform float colorType;
out vec2 texCoords;

out vec3 objectPosition;
out vec3 normalDirection;
out vec3 lightDirection;
out vec3 eyeVec;
out float lightDistance;

struct vec3Pair {
    vec3 first;
    vec3 second;
};

// Sfericke
vec3 graphulus(vec2 inPos) {
    inPos.x = mod(inPos.x, 1.0);
    inPos.y = mod(inPos.y, 1.0);
    float theta = inPos.x * 2.0 * PI;
    float phi = inPos.y * PI;
    float radius = 3.0 + cos(phi);
    float x = cos(theta) * radius ;
    float y = sin(theta) * radius;
    float z = sin(phi);

    return vec3(x, y, z);
}

vec3 graphulusNormal(vec2 inPos) {

    vec3 dx = graphulus(inPos + vec2(0.001, 0)) - graphulus(inPos - vec2(0.001, 0));
    vec3 dy = graphulus(inPos + vec2(0, 0.001)) - graphulus(inPos - vec2(0, 0.001));

    return cross(dx, dy);
}

vec3 lightSphere(vec2 vec) {
    float az = vec.x * PI;
    float ze = vec.y * PI / 2.0;
    float r = 0.1;

    float x = r * cos(az) * cos(ze);
    float y =  r * sin(az) * cos(ze);
    float z =  r * sin(ze);
    return  vec3( 6 * x, 6 *y, 6 * z);
}

// Kartezske
vec3 valec(vec2 inPos) {
    inPos.x *= 6.3f;
    inPos.y *= 6.3f;
    float r = 3.0 + sin(uTime);
    float x = cos(inPos.x) * r;
    float y = sin(inPos.x) * r;
    float z = inPos.y;
    return vec3(x* 0.5, y* 0.5, z* 0.5);
}

vec3 valecNormal(vec2 inPos) {

    vec3 dx = valec(inPos + vec2(0.001, 0)) - valec(inPos - vec2(0.001, 0));
    vec3 dy = valec(inPos + vec2(0, 0.001)) - valec(inPos - vec2(0, 0.001));

    return cross(dx, dy);
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

vec3 unknownNormal(vec2 inPos) {

    vec3 dx = unknown(inPos + vec2(0.001, 0)) - unknown(inPos - vec2(0.001, 0));
    vec3 dy = unknown(inPos + vec2(0, 0.001)) - unknown(inPos - vec2(0, 0.001));

    return cross(dx, dy);
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

vec3 gulaNormal(vec2 inPos) {

    vec3 dx = gula(inPos + vec2(0.001, 0)) - gula(inPos - vec2(0.001, 0));
    vec3 dy = gula(inPos + vec2(0, 0.001)) - gula(inPos - vec2(0, 0.001));

    return cross(dx, dy);
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

vec3 kuzelNormal(vec2 inPos) {

    vec3 dx = kuzel(inPos + vec2(0.001, 0)) - kuzel(inPos - vec2(0.001, 0));
    vec3 dy = kuzel(inPos + vec2(0, 0.001)) - kuzel(inPos - vec2(0, 0.001));

    return cross(dx, dy);
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

vec3 sombreroNormal(vec2 inPos) {

    vec3 dx = sombrero(inPos + vec2(0.001, 0)) - sombrero(inPos - vec2(0.001, 0));
    vec3 dy = sombrero(inPos + vec2(0, 0.001)) - sombrero(inPos - vec2(0, 0.001));

    return cross(dx, dy);
}

vec3Pair posCalc(vec2 inPosition) {
    vec3 pos1, pos2;
    vec3Pair result;
    int func = int(uFunction);
    switch (func) {
        //tato fukcia nema nastavene svetlo
        case 0: pos1 = graphulus(inPosition);
        pos2 = valec(inPosition);
        result.first = mix(pos1, pos2, uMorph);
        return result;
        case 2: result.first = graphulus(inPosition);
        result.second = graphulusNormal(inPosition);
        return result;
        case 3: result.first = valec(inPosition);
        result.second = valecNormal(inPosition);
        return result;
        case 4: result.first = unknown(inPosition);
        result.second = unknownNormal(inPosition);
        return result;
        case 5: result.first = gula(inPosition);
        result.second = gulaNormal(inPosition);
        return result;
        case 6: result.first = kuzel(inPosition);
        result.second = kuzelNormal(inPosition);
        return result;
        case 7: result.first = sombrero(inPosition);
        result.second = sombreroNormal(inPosition);
        return result;
        case 8: result.first = lightSphere(inPosition);
        return result;
        case 1: result.first =   3 * vec3(inPosition, 0.f);
                result.second =  vec3(0.f, 0.f, 1.f);
        return result;
        default: result.first = vec3(0.f, 0.f, 0.f);
        return result;
    }
}



void main() {
    texCoords = inPosition;
    vec2 position = inPosition * 2 - 1;
    vec3 object = 0.25 * posCalc(inPosition).first;
    vec3 normal = posCalc(inPosition).second;

    objectPosition = object;
    normalDirection = inverse(transpose(mat3(uModel))) * normal;

    vec4 finalPos4 = uModel * vec4(object,1.0);

    lightDirection = normalize(lightPosition - finalPos4.xyz);

    eyeVec = normalize(eyePosition - finalPos4.xyz);

    lightDistance = length(lightPosition - finalPos4.xyz);

    vec4 pos4 = vec4(object, 1.0);
    gl_Position = uProj * uView *  uModel * pos4;


}
