#version 300 es

#ifdef GL_ES
precision highp float;
#endif


uniform vec2 iResolution;
uniform int iFrame;
uniform vec2 iBase;
uniform vec2 iHead;
uniform vec2 iHand;
uniform sampler2D iTexture;
uniform float red;
uniform float green;
uniform float blue;

out vec4 fragColor;

float cross(vec2 a, vec2 b) {
    return a.x*b.y - a.y*b.x;
}

// gets the distance of a line to a point
float lineDist(vec2 pl1, vec2 pl2, vec2 pn) {
    vec2 p1 = normalize(pl2 - pl1);
    vec2 p2 = pn - pl1;
    float s = sign(cross(p1, p2));
    return s*length(p2 - p1*dot(p1, p2));
}

float cosNet(vec2 base, vec2 head, vec2 hand, vec2 uv) {
    float a = abs(lineDist(base, head, uv/2.0));
    float b = abs(lineDist(hand, head, uv/2.0));
    float c = float(int(a*255.0)^int(b*255.0))/255.0;
    return c;
}


void main() {

    vec2 uv = gl_FragCoord.xy/iResolution.xy;
    vec2 videoSpace = vec2(uv.x, -(uv.y-1.0))*0.5;

    vec4 colorT = texture(iTexture, videoSpace);

    vec2 headUV = vec2(iHead.x/iResolution.x, 1.0-(iHead.y/iResolution.y));
    vec2 baseUV = vec2(iBase.x/iResolution.x, 1.0-(iBase.y/iResolution.y));
    vec2 handUV = vec2(iHand.x/iResolution.x, 1.0-(iHand.y/iResolution.y));

    //vec4 color = vec4(vec3(cosNet(baseUV, headUV, handUV, uv)), 1.0);

    float progress = float(iFrame)/3600.0;

    vec4 h = vec4(vec3(cosNet(baseUV, headUV, handUV, uv+vec2(0.0, cos(50.0*progress)))), 1.0);
    vec4 color = vec4(0.0, 0.0, 0.0, 1.0);

    if (colorT.r > 0.95 && colorT.g > 0.95 && colorT.b > 0.95) {
        float d = (1.0-colorT.r) + (1.0-colorT.g) + (1.0-colorT.b);
        float l = (1.0-d/0.15);
        color = vec4(l, l, l, 1.0);
    }

    color = h*color.r + (1.0-h)*(1.0-color.r);
    color = vec4(pow(color.r, 0.5), pow(color.g, 0.5), pow(color.b, 0.5), 1.0);

    //color *= h;

    fragColor = color;

}