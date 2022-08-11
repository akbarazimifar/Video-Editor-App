precision mediump float;
varying highp vec2 textureCoordinate;
uniform sampler2D inputImageTexture;

const vec3 HEARTCOL=vec3(1., .05, .05);

uniform float time;
uniform vec2 resolution;

// Polynomial smooth max from IQ
float smax( float a, float b, float k ) {
	float h = clamp( (b-a)/k+.5,0.,1.);
	return mix( a, b, h ) + h*(1.-h)*k*.5;
}

float Heart(vec2 uv, float b) {
        float r=.25;
        b*=r;
         uv.x*=.7;
         float shape = smax(sqrt(abs(uv.x))*.5, b, .1);
         uv.y -= shape;
        uv.y += .1+b*.5;
     float d=length(uv);

    return S(r+b, r-b, d);
}

void main()
{
    vec4 texColor = texture2D(inputImageTexture, textureCoordinate);

    float T = (time / .99);
    vec2 position = (( textureCoordinate.xy / resolution.xy ) - 0.5);
    position.x *= resolution.x / resolution.y;
    vec3 color = texColor.rgb;
    vec2 uv = (textureCoordinate-resolution.xy*.5) / resolution.y;
    vec2 m = iMouse.xy/resolution.xy;

    vec3 col=vec3(0);
    float c = Heart(uv,m.y);
    col=vec3(c*HEARTCOL);
    gl_FragColor=vec4(col,1.0);

}
