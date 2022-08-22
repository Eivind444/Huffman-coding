package Oving12;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class HuffNode implements Comparable<HuffNode>{
    private int data;
    private Byte character = null;
    HuffNode left = null;
    HuffNode right = null;

    public HuffNode(int data, byte character){
        this.data = data;
        this.character = character;
    }

    public HuffNode(int data, HuffNode left, HuffNode right){
        this.data = data;
        this.left = left;
        this.right = right;
    }


    public int compareTo(HuffNode n) {
        return n.data - data;
    }

    public Byte getCharacter() {
        return character;
    }

    public int getData(){
        return data;
    }

}

public class Compress {
    byte[] data;
    private String codeString = "";
    ArrayList<ByteCode> paths = new ArrayList<>();

    public Compress(){
        try{
            Path fileLocation = Paths.get("text.txt");
            this.data = Files.readAllBytes(fileLocation);
        }catch (IOException e){

        }
    }

    class ByteCode{
        String code;
        byte byteNum;

        public ByteCode(String code, byte byteNum){
            this.code = code;
            this.byteNum = byteNum;
        }
    }

    public ArrayList<HuffNode> createNodes(){
        ArrayList<HuffNode> nodes = new ArrayList<>();
        ArrayList<Byte> bytes = new ArrayList<>();
        for (byte b : data){
            bytes.add(b);
        }
    loop:
        for (Byte b : bytes){
            for (HuffNode node : nodes){
                if (node.getCharacter().equals(b)){
                    continue loop;
                }
            }
            nodes.add(new HuffNode(Collections.frequency(bytes,b),b));
        }


        return nodes;
    }

    public ArrayList<HuffNode> createTree(){
        ArrayList<HuffNode> nodes = createNodes();
        Collections.sort(nodes);
        while (nodes.size() > 1){
            HuffNode newNode = new HuffNode(nodes.get(nodes.size()-1).getData() + nodes.get(nodes.size()-2).getData(), nodes.get(nodes.size()-1),nodes.get(nodes.size()-2));
            nodes.remove(nodes.get(nodes.size()-1));
            nodes.remove(nodes.get(nodes.size()-1));
            nodes.add(newNode);
            Collections.sort(nodes);
        }
        return nodes;
    }

    public void searchTree(HuffNode root, String s){
        if (root.left == null && root.right == null ){
            paths.add(new ByteCode(s,root.getCharacter()));
            //System.out.println(root.getCharacter() + ":" + s);
            return;
        }
        searchTree(root.left, s+"0");
        searchTree(root.right, s+"1");
    }

    public void encode(){
        for (byte b : data){
            for (int i = 0; i < paths.size(); i++){
                if (b == paths.get(i).byteNum){
                    codeString += paths.get(i).code;
                }
            }
        }

        String[] array = codeString.split("(?<=\\G.{8})");
        byte[] bytearr = new byte[array.length];
        for(int i = 0; i < array.length; i++) {
            bytearr[i] = (byte)Integer.parseInt(array[i], 2);
        }
        try{
            DataOutputStream outfile = new DataOutputStream(new FileOutputStream("zipped.zipzap"));
            outfile.write(bytearr);

        }catch (IOException e){
            e.printStackTrace();
        }
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter("freqTable.txt"));
            int[][] a = createFrequencyTable();
            for (int i = 0; i < a.length; i++){
                out.write(Integer.toString(a[i][0]));
                out.write(" ");
                out.write(Integer.toString(a[i][1]));
                out.newLine();

            }
            out.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public int[][] createFrequencyTable(){
        ArrayList<HuffNode> nodeList = createNodes();
        int[][] frequencyTable = new int[nodeList.size()][2];
        int counterX = 0;
        for (HuffNode node : nodeList){
            frequencyTable[counterX][0] = node.getCharacter();
            frequencyTable[counterX][1] = node.getData();
            counterX++;
        }
        return frequencyTable;
    }

    public static void main(String[] args) {
        Compress c = new Compress();
        c.createNodes();
        c.searchTree(c.createTree().get(0),"");
        c.encode();
        c.createFrequencyTable();
    }
}