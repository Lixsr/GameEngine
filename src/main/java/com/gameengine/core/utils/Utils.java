package com.gameengine.core.utils;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class Utils {
    public static FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        return buffer;
    }

//    public static IntBuffer storeDataInIntBuffer(int[] data) {
//        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
//        buffer.put(data).flip();
//        return buffer;
//    }
//
//    public static String loadResource(String filename) throws Exception {
//        String result;
//
//        try(InputStream in = Utils.class.getResourceAsStream(filename);
//            Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())) {
//            result = scanner.useDelimiter("\\A").next();
//        }
//
//        return result;
//    }
//
//    public static List<String> readAllLines(String fileName){
//        List<String> list = new ArrayList<>();
//
//        try(BufferedReader br = new BufferedReader(new InputStreamReader(Class.forName(Utils.class.getName()).getResourceAsStream(fileName)))){
//            String line;
//            while((line = br.readLine()) != null){
//                list.add(line);
//            }
//        } catch (IOException | ClassNotFoundException e){
//            e.printStackTrace();
//        }
//
//        return list;
//    }
}
