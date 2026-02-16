package util;

import renderer.Shader;
import renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {

    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();

    public static Shader getShader(String resourceName) {
        File file = new File(resourceName);
        String key = file.getAbsolutePath();
        if (shaders.containsKey(key)) {
            return shaders.get(key);
        } else {
            Shader shader = new Shader(resourceName);
            shader.compile();
            shaders.put(key, shader);
            return shader;
        }
    }

    public static Texture getTexture(String resourceName) {
        File file = new File(resourceName);
        String key = file.getAbsolutePath();
        if (textures.containsKey(key)) {
            return textures.get(key);
        } else {
            Texture texture = new Texture();
            texture.init(resourceName);
            textures.put(key, texture);
            return texture;
        }
    }
}
