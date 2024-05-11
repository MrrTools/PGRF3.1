#version 460
#define PI 3.1415926538

in vec2 inPosition;

uniform int uModel;
uniform float uScale;

out vec2 texCoords;

vec2 posCalc(vec2 inPosition) {
    int func = (uModel);
    switch (func) {
        case 0: return vec2(inPosition.x - 1, inPosition.y - 1);
        default: return vec2(inPosition.x, inPosition.y - 1);
    }
}

void main() {
    vec2 pos = posCalc(inPosition);
    gl_Position = vec4 (pos, 1.0, 1.0);
    texCoords = 1 - inPosition;
}