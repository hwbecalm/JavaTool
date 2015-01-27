package java_tool;

import java_tool.DES;

public class DES_Test {
	public static void main(String[] args) {
		String data="dfafdfde";
		String key="imdfsgrk";
		String data1="12356853";
		String key1="13564235";
		DES tt = new DES(key1);
		System.out.println("原始数据:  "+data1);
		
		byte[] data_encrypt = tt.encrypt(data1);
		System.out.println("加密后的数据:  ");
//		for(int i=0;i<data_en.length;i++)
//			System.out.print(data_en[i]);
		String bb = new String(data_encrypt);
		System.out.println(bb);
//		System.out.println("加密后的数据:  "+data_en.toString());
		
		byte[] data_decrypt = tt.decrypt(data_encrypt);
		System.out.println("解密后的数据:  ");
//		for(int i=0;i<data_de.length;i++)
//			System.out.print(data_de[i]);
		String aa = new String(data_decrypt);
		System.out.println(aa);
//		System.out.println("解密后的数据:  "+data_de.toString());
		
	}

}
