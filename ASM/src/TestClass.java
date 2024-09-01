
public class TestClass {

	public static void main(String[] args) {
		String num = "$12";
		System.out.println(Integer.toBinaryString(Integer.parseInt(num.replace("$", ""))));
	}

}
