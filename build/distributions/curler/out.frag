#version 300 es

#ifdef GL_ES
precision highp float;
#endif

#define pi 3.1415926535
#define tau 6.28318530718
#define phi 1.618033989

uniform vec2 iResolution;
uniform float iTime;
uniform float rotX;
uniform float rotY;
uniform float rotZ;
uniform float swirl;
uniform float slope;
uniform float size;
uniform float sep;

const float icos = atan(1.0, phi);
out vec4 fragColor;

vec3 rotate(vec3 v, vec3 axis, float angle) {
    vec3 k = normalize(axis);
    float s = sin(angle);
    float c = cos(angle);
    return v*c + cross(k, v)*s + k*dot(k, v)*(1.0-c);
}

bool spiral(vec3 uvw, float reverse) {
    uvw.xz /= slope*uvw.y;
    float r = length(uvw.xz);
    float a = r*swirl*reverse;
    if (uvw.y < 0.0 || uvw.y > 1.0 + abs(r-0.5)) { return false; }
    float f = mod(a, tau) - atan(uvw.z, uvw.x);
    if ( r < 1.01 && abs(mod(f + pi, tau)-pi) < 0.1  ) {
        return true;
    }

    return false;
}

float curler(vec2 uv) {
    bool hit = false;
    float r = 0.0;
    float d = 0.0;
    float reverse = 1.0;

    for(float z = 1.0; z > -1.0; z-=0.02) {
        vec3 uvw = vec3(uv, z)/size;
        uvw = rotate(uvw, vec3(0.0, 0.0, 1.0), -rotZ);
        uvw = rotate(uvw, vec3(1.0, 0.0, 0.0), rotY);
        uvw = rotate(uvw, vec3(0.0, 1.0, 0.0), iTime);

        vec3 _uvw = uvw;

        for (int j = 0; j < 5; j++) {
            for (int k = 0; k < 2; k++) {
                for (int i = 0; i < 3; i++) {

                    vec3 position1 = rotate(uvw, vec3(0.0, 1.0, 0.0), -pi/2.0);
                    position1 = rotate(position1, vec3(0.0, 0.0, 1.0), sep);
                    hit = spiral(position1, reverse);
                    if (hit) { return length(uvw)*0.5; }

                    vec3 position2 = rotate(uvw, vec3(0.0, 1.0, 0.0), pi/2.0);
                    position2 = rotate(position2, vec3(0.0, 0.0, 1.0), sep);
                    hit = spiral(position2, reverse);
                    if (hit) { return length(uvw)*0.5; }

                    uvw = rotate(uvw, vec3(1.0, 0.0, 0.0), pi/2.0);
                    uvw = rotate(uvw, vec3(0.0, 0.0, 1.0), pi/2.0);
                }
                uvw *= -1.0;
                reverse *= -1.0;
            }
            uvw = rotate(uvw, vec3(0.0, 1.0, 0.0), -pi/2.0);
            uvw = rotate(uvw, vec3(0.0, 0.0, 1.0), icos);
            uvw = rotate(uvw, vec3(0.0, 1.0, 0.0), tau/5.0);
            uvw = rotate(uvw, vec3(0.0, 0.0, 1.0), -icos);
            uvw = rotate(uvw, vec3(0.0, 1.0, 0.0), pi/2.0);
        }
    }
    return 0.0;

}

void main() {
    vec2 uv01 = gl_FragCoord.xy/iResolution.xy;
    vec2 uv = 2.0*uv01 - 1.0;
    vec3 color = vec3(250.0, 163.0, 40.0)/255.0;
    float r = curler(uv);

    if (r == 0.0) {
        color = vec3(0);
    } else {
        color = vec3(pow(color.r, 1.0/r), pow(color.g, 1.0/r), pow(color.b, 1.0/r));
    }

    fragColor = vec4(color, 1.0);
}