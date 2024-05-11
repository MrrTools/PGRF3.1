#version 460
in vec2 texCoords;

uniform int uModel;
uniform float uScale;
uniform float uMatrix;
uniform float uDiffusion;
uniform float uRandom;
uniform sampler2D textureForObjects;

float noiseConst = 0.5f;
float tresholdValue;

//matica pre order dithering
int ditherMat4[4][4] = {
{ 0, 12, 3, 15 },
{ 8, 4, 11, 7 },
{ 2, 14, 1, 13 },
{ 10, 6, 9, 5 }
};

out vec4 outColor;


//nahodne cislo pre random dithering
float random(vec2 xy)
{
    float noise = (fract(sin(dot(xy, vec2(12.9898, 78.233)*2.0)) * 43758.5453));
    return noise * noiseConst;
}

float computeThreshold(int uModel)
{

    switch (uModel) {
        //roztptyl
        case 1: return uDiffusion;
        break;
        //random
        case 2: return random(gl_FragCoord.xy) + uRandom;
        break;
        case 3:
        //matrix/order
        int x = int(mod(gl_FragCoord.x * uScale, 4.0f));
        int y = int(mod(gl_FragCoord.y * uScale, 4.0f));
        return (ditherMat4[x][y]+1)/16.0f;
        default : return 0.5f;
    }
}


void main() {
    vec3 textureColor = texture2D(textureForObjects, texCoords).rgb;

    vec3 color;

    if (uModel == 0) {
        outColor = vec4(textureColor, 1);
    } else {

        tresholdValue = computeThreshold(uModel);

        if (textureColor.r > tresholdValue) { color.r=1;
        }
        if (textureColor.g > tresholdValue) { color.g=1;
        }
        if (textureColor.b > tresholdValue) { color.b=1;
        }
        outColor = vec4(color, 1);
    }
}
