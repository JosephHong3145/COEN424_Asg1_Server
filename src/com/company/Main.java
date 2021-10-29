package com.company;
import com.google.gson.Gson;
import com.opencsv.CSVReader;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.*;
import java.util.Vector;

public class Main {
    //global context/variable
    static boolean isConnected = false;
    static Socket socket;
    static Gson gson;
    static Vector<CsvClass> csvObjectVector;
    static int serializationMode = 0;

    //file names
    static String dvd_testing = "csv//DVD-testing.csv";
    static String dvd_training = "csv//DVD-training.csv";
    static String ndb_testing = "csv//NDBench-testing.csv";
    static String ndb_training = "csv//NDBench-training.csv";

    public static void main(String arg[]){
        try {
            //initcontext
            gson = new Gson();
            ServerSocket server = new ServerSocket(3000); //socket number
            csvObjectVector = new Vector<CsvClass>();
            initCSV(dvd_testing);
            initCSV(dvd_training);
            initCSV(ndb_testing);
            initCSV(ndb_training);
            System.out.println("[SERVER]" + csvObjectVector.size() + " entries initialized. ");

            //waiting for client request
            System.out.println("[SERVER]Awaiting client...");
            socket = server.accept();
            isConnected = true;

            while(true){ //game loop
                if(!isConnected){ //reinitialize if connection is lost
                    socket = server.accept();
                    isConnected = true;
                }
                parseIncoming();
            }

        }catch(Exception e){
            System.out.println("[SERVER]Exception: " + e);
        }
    }

    public static void parseIncoming() throws IOException { //modules: parsing commands from clients
        String incoming = receiveString();
        String[] incomingParse = incoming.split(" ");

        if(incoming.equals("CONNECT")){
            sendString("CONNECTED");
            System.out.println("[SERVER]Client connected. ");
        }

        else if(incoming.equals("DISC")){
            System.out.println("[SERVER]Client disconnected. ");
            isConnected = false;
            socket.close();
        }

        else if(incoming.equals("TEST")){
            if(serializationMode == 0) {
                TestClass testClass = new TestClass("Joseph", "dot7201@gmail.com", 22, true);
                sendString("JSON TEST " + gson.toJson(testClass));
            }else if(serializationMode == 1){
                TestClass testClass = new TestClass("Joseph", "dot7201@gmail.com", 22, true);
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
                objOut.writeObject(testClass);
                byte[] byteArray = byteOut.toByteArray();
                sendString("BYTE TEST");
                if(receiveString().equals("RR BYTE")){
                    sendByte(byteArray);
                }
            }
        }

        else if(incomingParse[0].equals("RFW") || incoming.equals("RFW")){
            parseRFW(incoming);
        }

        else if(incoming.equals("SERIAL MODE TEXT")){
            serializationMode = 0;
            System.out.println("[SERVER]Binary mode set to text-based. ");
            System.out.println("[SERVER]serializationMode = " + serializationMode);
            sendString("RECEIVED");
        }

        else if(incoming.equals("SERIAL MODE BINARY")){
            serializationMode = 1;
            System.out.println("[SERVER]Binary mode set to binary-based. ");
            System.out.println("[SERVER]serializationMode = " + serializationMode);
            sendString("RECEIVED");
        }

        else{
            sendString("RECEIVED");
        }

    }

    public static void sendString(String s) throws IOException {//module: Sending string through a socket
        // get the output stream from the socket.
        OutputStream outputStream = socket.getOutputStream();
        // create a data output stream from the output stream so we can send data through it
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        // write the message we want to send
        dataOutputStream.writeUTF(s);
        dataOutputStream.flush(); // send the message
        System.out.println("[OUTGOING]" + s);

    }

    public static String receiveString() throws IOException {//module: receive string from socket
        // get the input stream from the connected socket
        InputStream inputStream = socket.getInputStream();
        // create a DataInputStream so we can read data from it.
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        // read the message from the socket
        String message = dataInputStream.readUTF();
        System.out.println("[INCOMING]" + message);
        return message;
    }

    public static void sendByte(byte[] byteArray) throws IOException {//module: sending byte array to socket
        // get the output stream from the socket.
        OutputStream outputStream = socket.getOutputStream();
        // create a data output stream from the output stream so we can send data through it
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        //first send byte length
        sendString(String.valueOf(byteArray.length));

        if(receiveString().equals("RR BYTEARRAY")) {
            // write the message we want to send
            dataOutputStream.write(byteArray);
            dataOutputStream.flush(); // send the message
        }

        System.out.print("[OUTGOING BYTES]");
        for(int i = 0; i < byteArray.length; i++){
            System.out.print(byteArray[i] + " ");
        }
        System.out.println();
    }

    public static byte[] receiveByte() throws IOException {//module: receive byte array from socket
        // get the input stream from the connected socket
        InputStream inputStream = socket.getInputStream();
        // create a DataInputStream so we can read data from it.
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        //first receive byte length
        int byteLength = Integer.parseInt(receiveString());
        sendString("RR BYTEARRAY");

        // read the message from the socket
        byte[] message = dataInputStream.readNBytes(byteLength);

        System.out.print("[INCOMING BYTES]");
        for(int i = 0; i < message.length; i++){
            System.out.print(message[i] + " ");
        }
        System.out.println();

        return message;
    }

    public static void initCSV(String fileName){ //module: converting all entries of csv into objects
        try(CSVReader reader = new CSVReader(new FileReader(fileName))) {
            String[] csvArray;
            boolean ignore_in = true;

            while ((csvArray = reader.readNext()) != null) {
                if(ignore_in){ //ignore first row.
                    ignore_in = false;
                }else {
                    String benchMarkType = " ";
                    String dataType = " ";

                    if (fileName.equals(dvd_testing)) {
                        benchMarkType = "DVD";
                        dataType = "Testing";
                    } else if (fileName.equals(dvd_training)) {
                        benchMarkType = "DVD";
                        dataType = "Training";
                    } else if (fileName.equals(ndb_testing)) {
                        benchMarkType = "NDB";
                        dataType = "Testing";
                    } else if (fileName.equals(ndb_training)) {
                        benchMarkType = "NDB";
                        dataType = "Training";
                    }

                    int CPUUtilization = Integer.parseInt(csvArray[0]);
                    float NetworkIn = Float.parseFloat(csvArray[1]);
                    float NetworkOut = Float.parseFloat(csvArray[2]);
                    double MemoryUnit = Double.parseDouble(csvArray[3]);
                    double FinalTarget = Double.parseDouble(csvArray[4]);
                    CsvClass csvObject = new CsvClass(benchMarkType, dataType, CPUUtilization, NetworkIn, NetworkOut, MemoryUnit, FinalTarget);
                    csvObjectVector.add(csvObject);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } ;

        System.out.println("[SERVER]Initialized CSV class for " + fileName);
    }

    public static void parseRFW(String incoming) throws IOException { //module: parsing RFW commands and sends client information
        String[] incomingParse = incoming.split(" ");
        boolean error = false;

        if(incomingParse.length < 7){ //incomplete command will not be executed
            sendString("RFW HELP");
            return;
        }

        int RFW_ID = 0; //unused
        String Benchmark_Type = incomingParse[1];
        String Metric_Type = incomingParse[2];
        int Batch_Unit = Integer.parseInt(incomingParse[3]);
        int Batch_ID = Integer.parseInt(incomingParse[4]);
        int Batch_Size = Integer.parseInt(incomingParse[5]);
        String Data_Type = incomingParse[6];

        //isolating Benchmark Type and Data Type
        //Consumes: csvObjectVector, Benchmark_Type, Data_Type
        //Produces: isolatedCsvVector
        Vector<CsvClass> isolatedCsvVector = new Vector<CsvClass>();
        for(int i = 0; i < csvObjectVector.size(); i++){
            if(csvObjectVector.get(i).getBenchMarkType().equals(Benchmark_Type)
                    && csvObjectVector.get(i).getDataType().equals(Data_Type)){
                isolatedCsvVector.add(csvObjectVector.get(i));
            }
        }

        if(isolatedCsvVector.size() == 0){ //if nothing got added, immediately return error
            sendString("RFW ERROR");
            return;
        }

        //splitting data into batches
        //Consumes: isolatedCsvVector, Batch_Unit, Metric_Type
        //Produces: csvBatchVector
        Vector<CsvClass_Batch> csvBatchVector = new Vector<CsvClass_Batch>();
        int i = 0; //csvBatchVector iterator
        int k = 1; //batch number counter
        while(i < isolatedCsvVector.size()){
            CsvClass_Batch newBatch = new CsvClass_Batch();
            for(int j = 0; j < Batch_Unit; j++){
                if(i >= isolatedCsvVector.size()){
                    break;
                }
                newBatch.addCsvClass(isolatedCsvVector.get(i));
                i++;
            }
            newBatch.setBatchID(k);
            newBatch.setWorkloadMetric(Metric_Type);
            csvBatchVector.add(newBatch);
            k++;
        }

        if(csvBatchVector.size() == 0){ //if nothing got batched, immediately return error
            sendString("RFW ERROR");
            return;
        }

        //selecting which batch to include in the response
        //Consumes: csvBatchVector, Batch_ID, Batch_Size
        //Produces: isolatedCsvBatchVector
        if(Batch_ID + Batch_Size - 1 >= csvBatchVector.size()){
            sendString("RFW ERROR");
            return;
        }
        Vector<CsvClass_Batch> isolatedCsvBatchVector = new Vector<CsvClass_Batch>();
        for(int a = Batch_ID - 1; a < Batch_ID + Batch_Size - 1; a++){
            isolatedCsvBatchVector.add(csvBatchVector.get(a));
        }

        //producing the string that can be sent to the client
        //Consumes: isolatedCsvBatchVector, RFW_ID
        //Produces: result
        int lastBatchID = isolatedCsvBatchVector.get(isolatedCsvBatchVector.size() - 1).getBatchID();
        String result = "RFD " + RFW_ID + " " + lastBatchID;
        if(serializationMode == 0) {
            for (int a = 0; a < isolatedCsvBatchVector.size(); a++) {
                result += " ";
                result += gson.toJson(isolatedCsvBatchVector.get(a));
            }
        }

        //display pipeline statistics
        System.out.println("[SERVER]Statistics for RFW ID " + RFW_ID + ": ");
        System.out.println("[SERVER]Total entries isolated: " + isolatedCsvVector.size());
        System.out.println("[SERVER]Total batches produced: " + csvBatchVector.size());
        System.out.println("[SERVER]Total batches sent: " + isolatedCsvBatchVector.size());

        //transmitting final result by segmentation
        //data structure: frame 0 = "RFD"; frame 1 = RFW ID; frame 2 = last batch ID; frame 3 and on = objects within batches
        //sending/receiving mechanism employs a simplified version of HDLC
        sendString("RFD"); //signals the beginning of transmission
        String[] RFDParse = result.split(" "); //splitting strings into frames and sending them

        int frameCount = 0;

        if(receiveString().equals("RR FRAMECOUNT")){
            if(serializationMode == 0) {
                frameCount = RFDParse.length;
            }else if(serializationMode == 1){
                frameCount = RFDParse.length + isolatedCsvBatchVector.size();
            }
            sendString(Integer.toString(frameCount)); //send frame count to client
        }

        if(receiveString().equals("RR RFD")) {
            if(serializationMode == 0) {
                for (int a = 0; a < RFDParse.length; a++) {
                    sendString(RFDParse[a]);
                    while (!receiveString().equals("RR NEXTFRAME")) {
                    }
                }
            }else if(serializationMode == 1){
                for (int a = 0; a < RFDParse.length; a++) {
                    sendString(RFDParse[a]);
                    while (!receiveString().equals("RR NEXTFRAME")) {
                    }
                }

                for(int a = 0; a < isolatedCsvBatchVector.size(); a++){

                    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                    ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
                    objOut.writeObject(isolatedCsvBatchVector.get(a));
                    byte[] byteArray = byteOut.toByteArray();
                    sendByte(byteArray);
                    while (!receiveString().equals("RR NEXTFRAME")) {
                    }
                }
            }
        }
        System.out.println("[SERVER]Result sent in " + frameCount + " frames");
    }

}
