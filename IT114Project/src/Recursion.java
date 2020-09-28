public class Recursion {

	public static int sum(int num) {
		if (num > 0) {
			return num + sum(num - 1);
		}
		return 0; // this is when num <= 0 so at sum 0 it should be 0.
		// so you need an exit condition in the if statement and an else statement for
		// what code it should return
	}

	public static void main(String[] args) {
		System.out.println(sum(10));
	}
}