package Oving12;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Decompress {
    byte[] data;
    String uncompString = "";
    String alldata = "";
    char[] bits = alldata.toCharArray();


    public Decompress(){
        try{
            Path fileLocation = Paths.get("zipped.zipzap");
            this.data = Files.readAllBytes(fileLocation);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void decomp(){
        try {
            DataInputStream inn = new DataInputStream(new BufferedInputStream(new FileInputStream("zipped.zipzap")));
            while (inn.available() > 0) {
                byte b = inn.readByte();
                this.alldata += Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
            }


        } catch(Exception e) {
            e.printStackTrace();
        }
        Compress c = new Compress();
        c.createNodes();
        ArrayList<HuffNode> hn = c.createTree();
    }

    public void createString(HuffNode root){
        ArrayList<Byte> bytes = new ArrayList<>();
        String decompressedData = "";
        HuffNode cur_node = root;
        int teller1 = 0;
        int teller2 = 0;

        for(char c: alldata.toCharArray()) {
            if (cur_node.getCharacter() != null){
                decompressedData += new String(new byte[]{cur_node.getCharacter()});
                bytes.add(cur_node.getCharacter());
                cur_node = root;
            }
            if(c == '0' && cur_node.left != null) {
                cur_node = cur_node.left;
                teller1++;
            }
            if(c == '1' && cur_node.right != null) {
                cur_node = cur_node.right;
                teller2++;
            }

        }
        System.out.println(decompressedData);
    }

    public static void main(String[] args) {
        Decompress d = new Decompress();
        d.decomp();
        Compress c = new Compress();
        c.createNodes();
        ArrayList<HuffNode> hn = c.createTree();
        d.createString(hn.get(0));
    }
}
