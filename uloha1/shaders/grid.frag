#version 330
in vec2 texCoords;

uniform sampler2D textureForObjects;

uniform float constantAttenuation = 1.0;
uniform float linearAttenuation = 0.1;
uniform float quadraticAttenuation = 0.01;
uniform float spotCutOff = 0.5f;
out vec4 outColor;
in vec3 objectPosition;
in vec3 normalDirection;
in vec3 lightDirection;
in vec3 eyeVec;
in float lightDistance;

uniform float colorType;
uniform vec3 lightPosition;
uniform vec3 eyePosition;
void main() {
    vec4 textureColor = texture(textureForObjects, texCoords);
    vec3 yld = normalize(lightDirection);
    vec3 vd = normalize(eyeVec);
    vec3 nd = normalize(normalDirection);

    vec3 yellowSpotDirection = -lightPosition;

    float yellowNDotL= max(dot(nd, yld), 0);

    vec3 yellowHalfVec = normalize(vd + yld);
    float yellowNDotH = pow(max(0, dot(nd, yellowHalfVec)), 16);

    float yellowAttentuation=1.0/(constantAttenuation +
    linearAttenuation * lightDistance +
    quadraticAttenuation * lightDistance * lightDistance);


    vec4 ambient = vec4(0.2, 0.2, 0.2, 1);

    vec4 yellowDiffuse = vec4(yellowNDotL*vec3(1.5), 1);
    vec4 yellowSpec = vec4(yellowNDotH*vec3(0.6), 1);

    yellowDiffuse = vec4(yellowDiffuse.rgb * vec3(0.8, 0.8, 0.255), 1.0f);
    yellowSpec = vec4(yellowSpec.rgb * vec3(0.8, 0.8, 0.255), 1.0f);


    float yellowSpotEffect = max(dot(normalize(yellowSpotDirection), normalize(-yld)), 0);
    float yellowBlend = clamp((yellowSpotEffect-spotCutOff)/(1-spotCutOff), 0.0, 1.0);//orezani na rozsah <0;1>

    vec4 lighting = ambient;

    if (colorType == 0) {
        outColor = textureColor;
    } else if (colorType == 1) {
        outColor = vec4(texCoords, 1.0, 1.0);
    } else if (colorType == 2) {
        outColor = vec4(objectPosition, 1.0);
    } else if (colorType == 3) {
        outColor = vec4(nd, 1.0);
    }else if (colorType == 4) {
        lighting = ambient  + yellowAttentuation * (yellowSpec + yellowDiffuse);
        outColor = lighting;
    }else if (colorType == 5) {
        lighting = ambient  + yellowAttentuation * (yellowSpec + yellowDiffuse);
        outColor = lighting* textureColor;
    }else if (colorType == 6){
        if (yellowSpotEffect > spotCutOff)  lighting = mix(ambient, ambient  + yellowAttentuation * (yellowSpec + yellowDiffuse), yellowBlend);
        outColor = lighting;
    }
    if (colorType == 7){
        if (yellowSpotEffect > spotCutOff)  lighting = mix(ambient, ambient  + yellowAttentuation * (yellowSpec + yellowDiffuse), yellowBlend);
        outColor = lighting * textureColor;
    }
}
