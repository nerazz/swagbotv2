package swagbot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Niklas Zd
 * @since 26.09.17
 */
public class Yolo {
	private static final Logger LOGGER = LogManager.getLogger(Yolo.class);

	int z1 = 3;
	int z2 = 10;

	public int add(int a, int b) {
		return a + b;
	}

	public int mult(int a, int b) {
		return a * b;
	}

	public int getInnerAdd() {
		return z1 + z2;
	}

	public int getInnerMult() {
		return z1 * z2;
	}

	public void setZ1(int z1) {
		this.z1 = z1;
	}

	public void setZ2(int z2) {
		this.z2 = z2;
	}
}
