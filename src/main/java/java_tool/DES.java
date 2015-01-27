package java_tool;

public class DES {
	byte[] key;
	public DES(String key){
		this.key = key.getBytes();
	}
	// 加密数据 开始置换的数组
	private static final int[] DATA_IP = {
		58, 50, 42, 34, 26, 18, 10, 2, 
        60, 52, 44, 36, 28, 20, 12, 4, 
        62, 54, 46, 38, 30, 22, 14, 6, 
        64, 56, 48, 40, 32, 24, 16, 8, 
        57, 49, 41, 33, 25, 17, 9,  1, 
        59, 51, 43, 35, 27, 19, 11, 3, 
        61, 53, 45, 37, 29, 21, 13, 5, 
        63, 55, 47, 39, 31, 23, 15, 7
	};	//64
	// 加密数据 最后的置换数组
	private static final int[] DATA_IP_1 = {
		40, 8, 48, 16, 56, 24, 64, 32, 
		39, 7, 47, 15, 55, 23, 63, 31,
		38, 6, 46, 14, 54, 22, 62, 30,
		37, 5, 45, 13, 53, 21, 61, 29,
		36, 4, 44, 12, 52, 20, 60, 28,
		35, 3, 43, 11, 51, 19, 59, 27,
		34, 2, 42, 10, 50, 18, 58, 26,
		33, 1, 41, 9,  49, 17, 57, 25,
	};	//64
	// 初始秘钥置换函数, 变成56位
	private static final int[] KEY_PC_1 = {
	   57, 49, 41, 33, 25, 17, 9,  
        1, 58, 50, 42, 34, 26, 18, 
       10,  2, 59, 51, 43, 35, 27, 
       19, 11,  3, 60, 52, 44, 36, 
       63, 55, 47, 39, 31, 23, 15,  
        7, 62, 54, 46, 38, 30, 22, 
       14,  6, 61, 53, 45, 37, 29, 
       21, 13,  5, 28, 20, 12,  4
	};	//56
	//获得的秘钥  进行的压缩和置换, 生成16组子秘钥必须的函数
	private static final int[] KEY_PC_2 = {
	   14, 17, 11, 24,  1,  5, 
        3, 28, 15,  6, 21, 10, 
       23, 19, 12,  4, 26,  8, 
       16,  7, 27, 20, 13,  2, 
       41, 52, 31, 37, 47, 55, 
       30, 40, 51, 45, 33, 48, 
       44, 49, 39, 56, 34, 53, 
       46, 42, 50, 36, 29, 32
	};	//48
	// 循环左移函数,用于生成16个子秘钥
	private static final int[] MOVE_LEFT = {
		1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1
	};
	// E盒运算, 扩展函数, 用于扩展 Ri（秘钥的右半部分）, 生成 16 个子秘钥  E(Ri)
	private static final int[] E = {
		32,  1,  2,  3,  4,  5, 
        4,   5,  6,  7,  8,  9, 
        8,   9, 10, 11, 12, 13, 
        12, 13, 14, 15, 16, 17, 
        16, 17, 18, 19, 20, 21, 
        20, 21, 22, 23, 24, 25, 
        24, 25, 26, 27, 28, 29, 
        28, 29, 30, 31, 32,  1
	};
	// P盒运算 用于参加16个子秘钥的运算。
	private static final int[] P = {
		16,  7, 20, 21, 
        29, 12, 28, 17,
         1, 15, 23, 26, 
         5, 18, 31, 10, 
         2,  8, 24, 14, 
         32, 27, 3,  9, 
         19, 13, 30, 6, 
         22, 11,  4, 25
	};
	// S盒函数, 这个函数用于 组成 半边子秘钥 取 1和6位 为X 取2345为Y, 找到对应的值, 刚好8组
	private static final int[][][] S_BOX = {
		{// S_Box[1]   OK
            { 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7 },
            { 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8 },
            { 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0 },
            { 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 }
        },
        { // S_Box[2]  OK
            { 15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10 },
            { 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5 },
            { 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15 },
            { 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 }
        },
        { // S_Box[3]  OK
            { 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8 },
            { 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1 },
            { 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7 },
            { 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12 }
        },
        { // S_Box[4]  OK
            { 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15 },
            { 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9 },
            { 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4 },
            { 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14 }
        },
        { // S_Box[5] OK
            { 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9 },
            { 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6 },
            { 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14 },
            { 11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3 }
        },
        { // S_Box[6] OK
            { 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11 },
            { 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8 },
            { 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6 },
            { 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13 }
        },
        { // S_Box[7] OK
            { 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1 },
            { 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6 },
            { 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2 },
            { 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12 }
        },
        { // S_Box[8] OK
            { 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7 },
            { 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2 },
            { 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8 },
            { 2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11 }
        } 
	};
	
	/** 第一步,生成初始化 置换数据 数组 */
	private int[] InitDataValue(int[] data){
		int[] dataValue = new int[64];
		for(int i=0;i<64;i++){
			dataValue[i] = data[ DATA_IP[i] - 1 ];
		}
		return dataValue;
	} 
	/** 第二步,生成子秘钥 */
	//生成16个 子秘钥
	private void InitKeyValue(int[] key, int[][] keyArray){
		int[] keyTemp = new int[56];
		for(int i=0;i<56;i++){
			keyTemp[i] = key[ KEY_PC_1[i] - 1];
		}
		//生成16个子秘钥
		for(int i=0; i<16; i++){
			// 左移秘钥是 56 位的
			LEFT_MOVE( keyTemp, MOVE_LEFT[i] );
			for(int j=0; j<48; j++){
				keyArray[i][j] = keyTemp[ KEY_PC_2[j] - 1 ];
			}
		}
	}
	// 秘钥左移函数
	private void LEFT_MOVE(int[] key, int offset){
		int[] keyLeft = new int[28];
		int[] keyRight = new int[28];
		int[] keyLeftTemp = new int[28];
		int[] keyRightTemp = new int[28]; 
		for(int i=0; i<28; i++){
			keyLeft[i] = key[i];
			keyRight[i] = key[i+28];
		}
		if(offset == 1){
			for(int i=0;i<27;i++){
				keyLeftTemp[i] = keyLeft[ i+1 ];
				keyRightTemp[i] = keyRight[ i+1 ];
			}
			keyLeftTemp[27] = keyLeft[0];
			keyRightTemp[27] = keyRight[0];
		}else if(offset == 2){
			for(int i=0;i<26;i++){
				keyLeftTemp[i] = keyLeft[ i+2 ];
				keyRightTemp[i] = keyRight[ i+2 ];
			}
			keyLeftTemp[26] = keyLeft[0];
			keyRightTemp[26] = keyRight[0];
			keyLeftTemp[27] = keyLeft[1];
			keyRightTemp[27] = keyRight[1];
		}
		// 重新组织 key 数组
		for (int i=0; i<28; i++) {
            key[i] = keyLeftTemp[i];
            key[i+28] = keyRightTemp[i];
        }
	}
	/** 第三步，秘钥函数 F */
	// 秘钥函数F, 接受32位 数据和48位秘钥的输入 , 包含S盒运算的整个过程
	private void loopF(int[] dataValue, int times, int flag, int[][] keyArray){
		int[] leftData = new int[32];
		int[] rightData = new int[32];
		int[] leftDataTemp = new int[32];
		int[] rightDataTemp = new int[32];
		int[] temp = new int[48];	//作为 中转的 48位数组存放过程
		int[] sBoxData = new int[8];
		int[] SValue = new int[32];
		int[][] SboxTemp = new int[8][6];
		int[] S_PValue = new int[32];
		for(int i=0;i<32;i++){
			leftData[i] = dataValue[i];
			rightData[i] = dataValue[ i+32 ];
		}
		for(int i=0; i<48; i++){
//			temp[i] = dataValue[ E[i] - 1 ];
			temp[i] = rightData[ E[i] - 1 ];
			//将上面的结果 同 48位秘钥Ki 做异或运算
			temp[i] = temp[i] + keyArray[times][i];
			if(temp[i] == 2)
				temp[i] = 0;
		}
		//分组成8份
		for(int i=0; i<8; i++){
			for(int j=0; j<6; j++){
				SboxTemp[i][j] = temp[ (i*6) + j ];
			}
			//做S盒变换
			sBoxData[i] = S_BOX[i]
					[ (SboxTemp[i][0]<<1) + SboxTemp[i][5] ]		//取第一位和第六位组合成X
					[ (SboxTemp[i][1]<<3) + (SboxTemp[i][2]<<2) + (SboxTemp[i][3]<<1) +SboxTemp[i][4] ];	//取第2-5位作为Y
			//换成二进制的  4位数
			for(int j=0; j<4; j++ ){
				SValue[((i * 4) + 3) - j] = sBoxData[i] % 2;
				sBoxData[i] = sBoxData[i] / 2;
			}
		}
		for(int i=0; i<32; i++){
			S_PValue[i] = SValue[ P[i] - 1 ];	//存储经过P置换的值
			leftDataTemp[i] = rightData[i]; 	// 右边移到左边
			rightDataTemp[i] = leftData[i] + S_PValue[i];
			if( rightDataTemp[i] ==2 )
				rightDataTemp[i] = 0;
			//重新组合 密文
			if( ((flag == 0) && (times == 0)) || ((flag == 1) && (times == 15)) ){
				dataValue[i] = rightDataTemp[i];
				dataValue[i+32] = leftDataTemp[i]; 
			}else{
				dataValue[i] = leftDataTemp[i];
				dataValue[i+32] = rightDataTemp[i];
			}
		}
	}
	/** 第四步，最后置换函数  */
	private int[] FinalDataValue(int[] data){
		int[] dataValue = new int[64];
		for(int i=0;i<64;i++){
			dataValue[i] = data[ DATA_IP_1[i] - 1 ];
		}
		return dataValue;
	}
	
	/**	执行 “加密-解密” 操作
     * @param timeData (int[64])二进制加密数据
     * @param flag 1或0，1为加密，0为解密
     * @param keyarray new int[16][48]
     * @return 长度为8的字节数组
     */
	private byte[] EncryptMain(int[] timeData, int flag, int[][] keyarray) {
        byte[] encrypt = new byte[8];
        int flags = flag;
        int[] M = new int[64];		//明文数组
        int[] MIP_1 = new int[64];	

        M = InitDataValue(timeData);	//初始化密文 IP
        
        if (flags == 1) { // 加密
            for (int i=0; i < 16; i++) {
            	loopF(M, i, flags, keyarray);//S盒处理
            }
        } else if (flags == 0) { // 解密
            for (int i = 15; i > -1; i--) {
            	loopF(M, i, flags, keyarray);//S盒处理
            }
        }
        
        MIP_1 = FinalDataValue(M);	//处理 IP_1
        
        //将（int[64]二进制数据字节数组，经过IP、S盒、IP-1处理后，得到的新的）int[64]二进制数据字节数组转换成byte[8]的字节数组
        GetEncryptResultOfByteArray(MIP_1, encrypt);
        // 返回加密数据
        return encrypt;
    }
	/**----------------------上面的是算法，下面的是输入的问题--------------------------*/

    /**	加密解密(主要方法)
     * @param des_key 密钥字节数组
     * @param des_data 要处理的数据字节数组
     * @param flag (1或0)，1为加密，0为解密
     * @return 处理后的数据
     */
    private byte[] DesEncrypt(byte[] des_key, byte[] des_data, int flag) {
    	byte[] format_data = ByteDataFormat(des_data);		//补齐原始数据字节数组的长度为8的倍数，不足元素用0补
        byte[] format_key = ByteDataFormat(des_key);		//补齐密钥字节数组的长度为8的倍数，不足元素用0补
        int datalen = format_data.length;						//补齐后的原始数据字节数组的长度
        int unitcount = datalen / 8;							//补齐后的原始数据字节数组长度是8的多少倍
        byte[] result_data = new byte[datalen];					//用于盛放加密后的结果
        //每一次循环，都操作8个字节（加密解密）
        for (int i = 0; i < unitcount; i++) {
            byte[] tmpkey = new byte[8];			//真正起作用的密钥字节数组，只有8个字节
            byte[] tmpdata = new byte[8];			//用于参与操作的数据字节数组，只有8个字节
            System.arraycopy(format_key, i * 8, tmpkey, 0, 8);
            System.arraycopy(format_data, i * 8, tmpdata, 0, 8);
            byte[] tmpresult = UnitDes(tmpkey, tmpdata, flag);		//执行操作
            System.arraycopy(tmpresult, 0, result_data, i * 8, 8);
        }
        return result_data;
    }
    
    /** @param des_key 8个字节的密钥字节数组
     * @param des_data 8个字节的数据字节数组
     * @param flag 1或0，1为加密，0为解密
     * @return 8个字节的字节数组
     */
    private byte[] UnitDes(byte[] des_key, byte[] des_data, int flag) {
        // 检测输入参数格式是否正确，错误直接返回空值（null）
        if ((des_key.length != 8) ||  (des_data.length != 8) || ((flag != 1) && (flag != 0))) {
            throw new RuntimeException("Data Format Error !");
        }
        int flags = flag;
        int[] keyData = new int[64];	        // 二进制加密密钥
        int[] encryptdata = new int[64];        // 二进制加密数据
        byte[] EncryptCode = new byte[8];        // 加密操作完成后的字节数组
        int[][] KeyArray = new int[16][48];		        // 密钥初试化成二维数组
        
        // 将密钥字节数组转换成二进制字节数组
        keyData = ReadDataToBirnaryIntArray(des_key);
        // 将加密数据字节数组转换成二进制字节数组
        encryptdata = ReadDataToBirnaryIntArray(des_data);
        // 初试化密钥为二维密钥数组 (3个子秘钥全部都初始化了)
        InitKeyValue(keyData, KeyArray);
//         执行加密解密操作
        EncryptCode = EncryptMain(encryptdata, flags, KeyArray);
        return EncryptCode;
    }
    
    /** DES加密
     * @param data 原始数据(长度不能超过9999位)
     * @return 加密后的数据字节数组
     */
    public byte[] encrypt(String data){		//先不考虑填充问题
	     byte[] bytekey = key;
	     byte[] bytedata = data.getBytes();
	     byte[] result = new byte[data.length()];
	     result = DesEncrypt(bytekey, bytedata, 1);
	     return result;
    }
    
    /** DES解密
     * @param encryptData 加密后的数据字节数组
     * @return 还原后的数据字符串
     */
    public byte[] decrypt(byte[] encryptData){
        byte[] bytekey = key;
        //这里还得注意一下填充问题,先不考虑填充问题
        byte[] result = new byte[encryptData.length];
        result = DesEncrypt(bytekey, encryptData, 0);	//解密处理
        
        return result;
   }
    
    /**
     * 格式化字节数组，使其的长度为8的倍数，那些不足的部分元素用0填充
     * @return 一个新的字节数组，其长度比原数组长1-8位	(在byte的情况增加了7位，好像并没有表现出来)
     */
    private byte[] ByteDataFormat(byte[] data) {
        int len = data.length;
        int newlen;
        int padlen = 8 - (len % 8);		//要格式化的字节数组的长度与8的倍数的差值
        if(padlen !=8 ){
        	newlen = len + padlen;
        }else{
        	newlen = len;
        }
        byte[] newdata = new byte[newlen];
        System.arraycopy(data, 0, newdata, 0, len);
        for (int i = len; i < newlen; i++)
            newdata[i] = 0;
        return newdata;
    }
    
    /** 转换8个字节长度的数据字节数组为二进制数组
     * (一个字节转换为8个二进制)
     * @param intdata 8个字节的数据字节数组
     * @return 长度为64的二进制数组
     */
    private int[] ReadDataToBirnaryIntArray(byte[] intdata) {
        int i;
        int j;
        // 将数据转换为二进制数，存储到数组	（这里我真是觉得很蛋疼啊？？~~直接变成了16位）
        int[] IntDa = new int[8];
        for (i = 0; i < 8; i++) {
            IntDa[i] = intdata[i];		//intdata[i]为byte,范围是-128~127
            if (IntDa[i] < 0) {			//故：IntDa[i]范围是-128~127
                IntDa[i] += 256;		//IntDa[i]永远不会超过256
                IntDa[i] %= 256;		//所以该处不需要取模，取模后结果还是自己
            }
        }
        
        int[] IntVa = new int[64];
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                IntVa[((i * 8) + 7) - j] = IntDa[i] % 2;
                IntDa[i] = IntDa[i] / 2;
            }
        }
        return IntVa;
    }
    
    /**	int[64]二进制数据字节数组转换成byte[8]的字节数组
     * @param data int[64]二进制数据字节数组
     * @param value byte[8] byte[8]的字节数组
     */
    private void GetEncryptResultOfByteArray(int[] data, byte[] value) {
        int i;
        int j;
        // 将存储64位二进制数据的数组中的数据转换为八个整数（byte）
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                value[i] += (byte)(data[(i << 3) + j] << (7 - j));
            }
        }
        for (i = 0; i < 8; i++) {
            value[i] %= 256;
            if (value[i] > 128) {
                value[i] -= 255;
            }
        }
    }
}
