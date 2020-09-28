//asc52

public class RecursionToLoopHW {
	public static int sum(int num) {
		int x = 0; // initialize a variable that holds the sum
		System.out.println("Recursion to Loop");
		for (int i = num; i > 0; i--) { // initialize the for variable to the number we are calling
			x += i; // add i, which decrements every time its called, to the sum x
			System.out.println(x); // to show my work
		}
		System.out.println("");
		return x;
	}

	public static void main(String[] args) {
		System.out.println("sum(10): " + sum(10));
	}
}