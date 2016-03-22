/*
 * Illumination fragment shader: phong and toon shading
 */

// From vertex shader: position and normal (in eye coordinates)
varying vec4 pos;
varying vec3 norm;

// Do Phong specular shading (r DOT v) instead of Blinn-Phong (n DOT h)
uniform int phong;
// Do toon shading
uniform int toon;
// If false, then don't do anything in fragment shader
uniform int useFragShader;

// Toon shading parameters
uniform float toonHigh;
uniform float toonLow;

// Apply volume texture to diffuse term
//uniform int volTexture;
// Volume texture scale
//uniform float volRes;

// Compute toon shade value given diffuse and specular component levels
vec4 toonShade(float diffuse, float specular)
{
    // ... (placeholder)
  	if(specular > toonHigh)
  	{
  		return vec4(1.0,1.0,1.0,1.0);
  	}
  	
	if(diffuse < toonLow)
	{
		diffuse = 0.2;
	}
	else if(diffuse > toonHigh)
	{
		diffuse = 0.4;
	}
	else
	{
		diffuse= 0.3;
	}
  		
    return gl_FrontLightProduct[0].ambient + gl_FrontLightProduct[0].diffuse * diffuse;
}

void main()
{
    if (useFragShader == 0) {
        // Pass through
        gl_FragColor = gl_Color;
    } else {
        // Do lighting computation...
		vec4 resultColor = vec4(0.0,0.0,0.0,0.0);
		resultColor = gl_FrontLightModelProduct.sceneColor + gl_FrontLightProduct[0].ambient;
        
        // ...
        vec4 norm4 = vec4(normalize(norm), 0.0);
        vec4 l = normalize(gl_LightSource[0].position - pos);
    	vec4 v = normalize(-pos);
    	float nDotl = dot(norm4, l);
    	float d = max(0.0, nDotl);
    	
    	vec4 diffIntensity =  gl_FrontLightProduct[0].diffuse * d;
    	diffIntensity = clamp(diffIntensity, 0.0, 1.0);
    
    	vec4 specIntensity = vec4(0.0,0.0,0.0,0.0);
    	float s = 0.0;
    	if(nDotl > 0.0)
    	{
    		float nDotHf = 0.0;
    		if(phong == 1)
        	{
        		vec4 r = normalize(reflect(-l,norm4));
        		float rDotv = dot(r, v);
        		float alphaNew = gl_FrontMaterial.shininess / 4.0;
        		s = pow(max(0.0, rDotv), alphaNew);
        	}
        	else
        	{
        		vec4 halfVector = normalize(l + v);
    			nDotHf = dot(halfVector,vec4(norm,0.0));
    			s = pow(max(0.0,nDotHf), gl_FrontMaterial.shininess);
        	}
    		
    		specIntensity = gl_FrontLightProduct[0].specular * s;
    		specIntensity = clamp(specIntensity, 0.0, 1.0);
    	}
               
        
        if (toon == 1) {
            // ... (placeholder)            
            gl_FragColor = toonShade(d,s);
        } else {
        	
            // ... (placeholder)
            gl_FragColor = resultColor + diffIntensity + specIntensity;
        }
    }
}
