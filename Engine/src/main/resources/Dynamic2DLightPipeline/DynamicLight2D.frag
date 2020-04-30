#version 400 core

#define PI 3.14
const float THRESHOLD = .75;

in vec2 pass_texCoord;

uniform sampler2D uni_texture;
uniform float uni_size = 1.0;// 1.0 = max
uniform vec2 uni_resolution = vec2(256, 256);

out vec4 out_fragColor;

void main()
{
    float distance = uni_size;

    for (float y = 0.0; y < uni_resolution.y; y += 1.0)
    {
        vec2 norm = vec2(-pass_texCoord.x, y / uni_resolution.y) * 2.0 - 1.0;
        float theta = PI * 1.5 + norm.x * PI;
        float r = (1.0 + norm.y) * 0.5;

        vec2 coord = vec2(-r * sin(theta), -r * cos(theta)) / 2.0 + 0.5;
        vec4 data = texture2D(uni_texture, coord);

        float dst = y / uni_resolution.y;
        float caster = data.w;

        if (caster > THRESHOLD)
        {
            distance = min(distance, dst);
        }
    }

    out_fragColor = vec4(vec3(distance), 1.0);
}