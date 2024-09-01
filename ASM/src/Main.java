import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

	private HashMap<String, int[]> AsmDesign;
	private ArrayList<String[]> AsmFile;
	private String finalBinStr = "";

	private String HK8_DESIGN = "00:NOP-00\n" + "01:ADD-00\n" + "02:SUB-00\n" + "03:LDA-11\n" + "04:STA-11\n"
			+ "05:LIA-08\n" + "06:LDB-11\n" + "07:STB-11\n" + "08:LIB-08\n" + "09:STS-11\n" + "10:MEM-11\n"
			+ "11:JMP-11\n" + "12:JPZ-11\n" + "13:JPC-11\n" + "14:OUT-11\n" + "15:OTI-08\n" + "16:PLH-00\n"
			+ "17:PLH-00\n" + "18:PLH-00\n" + "19:PLH-00\n" + "20:PLH-00\n" + "21:PLH-00\n" + "22:PLH-00\n"
			+ "23:PLH-00\n" + "24:PLH-00\n" + "25:PLH-00\n" + "26:PLH-00\n" + "27:PLH-00\n" + "28:PLH-00\n"
			+ "29:PLH-00\n" + "30:RST-00\n" + "31:HLT-00";

	public static void main(String[] args) throws Exception {
		// get asm design int {id, arg}
		// get HK8 file as array of str {inst, arg}
		// convert to bin
//		new Main("/home/hridaykh/Code/HK8/ASM/Test.HK8", "/home/hridaykh/Code/HK8/ASM/AsmDesign.HK8D");
		if (args.length == 1) {
			new Main(args[0]);
		} else {
			Scanner scanner = new Scanner(System.in);
			System.out.print("HK8 Assembly File Location: ");
			String asmLocation = scanner.nextLine();
			scanner.close();
			new Main(asmLocation);
		}
	}

	public Main(String UsrAsmFile) throws Exception {
		AsmDesign = getAsmDesign(); // Tested And Works
		AsmFile = getAsmFile(new File(UsrAsmFile));
		finalBinStr = processFile();
		File file = new File(UsrAsmFile + "B");
		file.createNewFile();
		FileWriter fw = new FileWriter(UsrAsmFile + "B");
		fw.write(finalBinStr.replace("\n", ""));
		fw.close();

	}

	private HashMap<String, int[]> getAsmDesign() throws Exception {
		String[] file = HK8_DESIGN.split("\n");

		HashMap<String, int[]> readFile = new HashMap<String, int[]>();

		for (int i = 0; i < 32; i++) {

			String[] splitFile = file[i].split(":")[1].split("-");

			int[] intray = { i, Integer.valueOf(splitFile[1]) };

			readFile.put(splitFile[0], intray);
		}
		return readFile;
	}

	private ArrayList<String[]> getAsmFile(File asmFile) throws Exception {
		Scanner scanner = new Scanner(asmFile);
		ArrayList<String> fileList = new ArrayList<String>();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			fileList.add(line);
		}
		scanner.close();
		String[] file = fileList.toArray(String[]::new);
		ArrayList<String[]> outFile = new ArrayList<String[]>();

		for (String ln : file) {
			String[] lnA = ln.split(";")[0].trim().split("\\s+");
			Boolean skipThisLine = false;
			switch (lnA.length) {
			case 0:
				skipThisLine = true;
			case 1:
				String[] tmp = { lnA[0], "" };
				outFile.add(tmp);
				break;
			case 2:
				String[] Tmp = { lnA[0], processArgNum(lnA[1]) };
				outFile.add(Tmp);
				break;
			default:
				throw new Exception("Prob in usr line: " + ln);
			}
			if (skipThisLine) {
				continue;
			}
		}

		return outFile;
	}

	private String processArgNum(String num) throws Exception {
		switch (num.charAt(0)) {
		case '$': // base-10 to bin
			return Integer.toBinaryString(Integer.parseInt(num.replace("$", "")));
		case '#':
			return new BigInteger(num.replace("#", ""), 16).toString(2);
		case '0':
		case '1':
			return num;
		default:
			throw new Exception("Prob in number: " + num);
		}
	}

	private String processFile() throws Exception {
		String file = "";
		String nLn = "";
		for (String[] line : AsmFile) {
			if (line[0].isBlank())
				continue;
			int[] iInf = AsmDesign.get(line[0]);
			String iBin = "";
			try {
				iBin = Integer.toBinaryString(iInf[0]);
			} catch (NullPointerException e) {
				throw new Exception("Invalid Instruction: " + line[0]);
			}
			while (iBin.length() != 5)
				iBin = "0" + iBin;

			String arg = line[1];
			while (arg.length() != 11)
				arg = "0" + arg;
			nLn = iBin + arg;
			file = file + nLn + "\n";
		}
		return file;
	}

}
