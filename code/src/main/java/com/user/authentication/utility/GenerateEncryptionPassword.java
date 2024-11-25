package com.user.authentication.utility;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


public class GenerateEncryptionPassword {
	public static final String AES = "AES";
	private static String key = "DB99A2A8EB6904F492E9DF0595ED623C";

    private static String byteArrayToHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    private static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }
    
    public static String encryptPassword(String password) {
    	
    	 byte[] bytekey = hexStringToByteArray(key);
         SecretKeySpec sks = new SecretKeySpec(bytekey, GenerateEncryptionPassword.AES);
         Cipher cipher;
         String encryptedpwd = "";
		try {
			cipher = Cipher.getInstance(GenerateEncryptionPassword.AES);
			 cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());
	         byte[] encrypted = cipher.doFinal(password.getBytes());
	         encryptedpwd = byteArrayToHexString(encrypted);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    	
    	return encryptedpwd;
    	
    }
    
    public static String decryptPassword(String password) {
    	byte[] bytekey = hexStringToByteArray(key);
        SecretKeySpec sks = new SecretKeySpec(bytekey, GenerateEncryptionPassword.AES);
        Cipher cipher;
        String OriginalPassword = "";
		try {
			cipher = Cipher.getInstance(GenerateEncryptionPassword.AES);
			cipher.init(Cipher.DECRYPT_MODE, sks);
		    byte[] decrypted = cipher.doFinal(hexStringToByteArray(password));
		    OriginalPassword = new String(decrypted);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        return OriginalPassword;
    }
    
    public static String encryptPasswordByBcryptEncoder(String password) {
    	PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); 
        String encodedPassword = passwordEncoder.encode(password);  
        System.out.print(encodedPassword);
        
        return encodedPassword;
    	
    }
    
    public static Boolean matchPassword(String password, String encodedPassword) {
    	PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); 
    	Boolean match = passwordEncoder.matches(password, encodedPassword);
    	
    	return match;
    }
    
    
    public static void main (String[] args) {
    	String password = "123456"; 
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); 
        String encodedPassword = passwordEncoder.encode(password);  
        System.out.print(encodedPassword);
        System.out.println("\n" +passwordEncoder.matches("123456", encodedPassword));
        

    }
    
  

}