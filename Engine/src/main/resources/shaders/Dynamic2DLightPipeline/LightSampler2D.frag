#version 400 core

#define PI 3.14

in vec2 pass_texCoord;

uniform vec4 uni_color = vec4(1.0, 1.0, 1.0, 1.0);
uniform float uni_size = 1.0;
uniform float uni_intensity = 1.0;
uniform sampler2D uni_texture;
uniform vec2 uni_resolution = vec2(256, 256);

out vec4 out_fragColor;

float sampleTexture(vec2 coord, float r) {
    return step(r, texture2D(uni_texture, coord).r);
}

void main()
{
    vec2 norm = pass_texCoord.st * 2.0 - 1.0;
    float theta = atan(norm.y, norm.x);
    float r = length(norm);
    float coord = (theta + PI) / (2.0*PI);

    vec2 tc = vec2(coord, 0.0);

    float center = sampleTexture(vec2(tc.x, tc.y), r);

    float blur = (1.0 / uni_resolution.x)  * smoothstep(0.0, 1.0, r);

    float sum = 0.0;

    sum += sampleTexture(vec2(tc.x - 4.0*blur, tc.y), r) * 0.05;
    sum += sampleTexture(vec2(tc.x - 3.0*blur, tc.y), r) * 0.09;
    sum += sampleTexture(vec2(tc.x - 2.0*blur, tc.y), r) * 0.12;
    sum += sampleTexture(vec2(tc.x - 1.0*blur, tc.y), r) * 0.15;
    sum += center * 0.16;
    sum += sampleTexture(vec2(tc.x + 1.0*blur, tc.y), r) * 0.15;
    sum += sampleTexture(vec2(tc.x + 2.0*blur, tc.y), r) * 0.12;
    sum += sampleTexture(vec2(tc.x + 3.0*blur, tc.y), r) * 0.09;
    sum += sampleTexture(vec2(tc.x + 4.0*blur, tc.y), r) * 0.05;

    sum *= uni_intensity;

    float softShadows = 1.0;
    float lit = mix(center, sum, softShadows) * smoothstep(uni_size, 0.0, r);

    out_fragColor = uni_color * vec4(vec3(lit), lit);
}