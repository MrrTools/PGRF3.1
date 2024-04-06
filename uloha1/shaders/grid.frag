#version 330

in vec3 fragPos;
in vec3 Normal;
in vec2 TexCoords;

uniform vec3 viewPos;
uniform vec3 surfaceColor;
uniform sampler2D textureForObjects;
uniform vec3 lightPos;
uniform float colorType;

out vec4 outColor;

void main() {
    vec3 lightDir = normalize(lightPos - fragPos);

    float distance = length(lightPos - fragPos);
    vec4 lightDistanceColor = vec4(vec3(distance), 1.0);

    float diff = max(dot(Normal, lightDir), 0.0);
    vec3 diffuse = diff * vec3(1.0, 1.0, 1.0);

    vec3 ambient = 0.1 * vec3(1.0, 1.0, 1.0);

    vec3 viewDir = normalize(viewPos - fragPos);
    vec3 reflectDir = reflect(-lightDir, Normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
    vec3 specular = spec * vec3(1.0, 1.0, 1.0);// Use white light for simplicity

    float constant = 1.0;
    float linear = 0.09;
    float quadratic = 0.032;
    float attenuation = 1.0 / (constant + linear * distance + quadratic * distance * distance);


    vec4 textureColor = texture(textureForObjects, TexCoords);
    vec4 texCoordsColor = vec4(TexCoords, 0.5, 1.0);


    vec4 positionColor = vec4(fragPos, 1.0);

    float depth = length(viewPos - fragPos);
    vec4 depthColor = vec4(vec3(depth), 1.0);


    vec3 dx = vec3(dFdx(fragPos.x), dFdx(fragPos.y), dFdx(fragPos.z));
    vec3 dy = vec3(dFdy(fragPos.x), dFdy(fragPos.y), dFdy(fragPos.z));

    vec3 Normal = normalize(cross(dx, dy));


    vec3 totalLight = ambient + diffuse + specular;

    vec3 finalColor = totalLight * textureColor.rgb;

    if (colorType == 0) {
        outColor = textureColor;
    } else if (colorType == 1) {
        outColor = texCoordsColor;
    } else if (colorType == 2) {

    } else if (colorType == 3) {
        outColor = textureColor;
    } else if (colorType == 4) {
        outColor = lightDistanceColor;
    } else if (colorType == 5) {
        outColor = positionColor;
    } else if (colorType == 6) {
        outColor = depthColor;
    }
    else if (colorType == 7) {
        outColor = vec4(finalColor, textureColor.a);
    } else {
        outColor = vec4(1.0, 0.0, 0.0, 1.0);
    }
}