import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.security.*;

public class ComputeSHA {
    
    public static void main(String[] args) throws Exception {
        
        String fileName="";
        //get parameter
        if(args.length > 0) {
     		fileName=args[0];
        }	else {
			System.out.println("No parameters found!");
        	System.exit(0);
        }

        //encrpyt
        File file = new File(fileName);
        FileInputStream fs = new FileInputStream(file);
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        
        //get message from file
        String msg="";
        int ch = 0;
        while ((ch = fs.read()) != -1) {
            msg+=(char) ch;
        };
        
        md.update(msg.getBytes());
        byte[] output = md.digest();
        
        //convert byte to hex format
        StringBuffer hex = new StringBuffer();
        for (int i=0; i<output.length; i++) {
            hex.append(Integer.toString((0xFF & output[i] ) + 0x100, 16).substring(1));
        }
        
        System.out.printf(hex.toString());

    }

}