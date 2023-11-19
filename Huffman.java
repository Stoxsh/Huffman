import java.util.Scanner;

public class Huffman {

	private Queueable<Node> pq;
	private Node root;

	public Huffman() {}

	public String encode(String msg) {
		heap(msg);
		buildHuffman();
		String encodeResult = "";
		if (root.left == null && root.right == null) {
			for (int i = 0; i < msg.length(); i++) {
				encodeResult = encodeResult + "0";
			}
		} else {
			String[] msgList = msg.split("");
			String[] encode = new String[msgList.length];
			for (int i = 0; i < msgList.length; i++) {
				Node temp = root;
				encode[i] = encodeHelper(msgList[i], temp, "");
			}
			for (int i = 0; i < encode.length; i++) {
				encodeResult = encodeResult + encode[i];
			}
		}
		return encodeResult;
		
	}

	public String encodeHelper(String c, Node node, String encode) {
		if (node.left == null && node.right == null) {
			if (node.data.equals(c)) {
				return encode;
			} else {
				return "";
			}
		}
		String leftEncode = encodeHelper(c, node.left, encode + "0");
		String rightEncode = encodeHelper(c, node.right, encode + "1");
		if (!(leftEncode.isEmpty())) {
			return leftEncode;
		}
		if (!(rightEncode.isEmpty())) {
			return rightEncode;
		}
		return "";
	}

	public void buildHuffman() {
		while (true) {
			try {
				Node extractTest1, extractTest2;
				extractTest1 = pq.dequeue();
				root = extractTest1;
				extractTest2 = pq.dequeue();
				pq.enqueue(extractTest1);
				pq.enqueue(extractTest2);
			} catch (Exception e) {
				break;
			}
			Node a = pq.dequeue();
			Node b = pq.dequeue();
			Node internal = new Node("foo" , a.freq + b.freq, a, b, null);
			a.parent = internal;
			b.parent = internal;
			pq.enqueue(internal);
		}
	}

	public void heap(String msg) {
		pq = new HeapPriorityQueue();
		String[] msgArr = msg.split("");
		for (int i = 0; i < msgArr.length; i++) {
			if (msgArr[i] == null) {
				continue;
			}
			String str = msgArr[i];
			int freq = 1;
			for (int j = i + 1; j < msgArr.length; j++) {
				if (msgArr[i].equals(msgArr[j])) {
					msgArr[j] = null;
					freq++;
				}
			}
			pq.enqueue(new Node(str, freq, null, null, null));
		}
	}
	
	public String decode(String code) {
		String decode = "";
		if (root.left == null && root.right == null) {
			for (int i = 0; i < code.length(); i++) {
				decode = decode + root.data;
			}
		} else {
			Node temp;
			int binSize;
			String charAndNum, c;
			String tempCode = new String(code);
			while (!(tempCode.isEmpty())) {
				temp = root;
				charAndNum = decodeHelper(temp, tempCode, 0);
				c = charAndNum.substring(0, 1);
				binSize = Integer.parseInt(charAndNum.substring(1));
				decode = decode + c;
				tempCode = tempCode.substring(binSize);
			}
		}
		return decode;
	}

	public String decodeHelper(Node node, String code, int size) {
		if (node.left == null && node.right == null) {
			return node.data + size;
		}
		if (code.charAt(0) == '0') {
			node = node.left;
			size++;
		}
		if (code.charAt(0) == '1') {
			node = node.right;
			size++;
		}
		String d = decodeHelper(node, code.substring(1) , size);
		return d;
	}
	
	public String toBinary(String s) {
		String ret = "";
		for(int i = 0; i < s.length(); i++) 
			ret = toBinary(s.charAt(i)) + ret;
		return ret;
	}
	
	private String toBinary(int ch) {
		ch = 0xFFFF & ch;
		String ret = "";
		for(int i = 0; i < 16; i++) {
			ret = (ch & 1) + ret;
			ch = ch >> 1;
		}
		return ret;
	}
	
	public static void main(String[] args) {
		Huffman h = new Huffman();
		Scanner s = new Scanner(System.in);
		System.out.println("Enter message that you want to encode:");
		String msg = s.nextLine();
		h.encode(msg);
		System.out.println("Original binary:\n" + h.toBinary(msg));
		String code = h.encode(msg);
		System.out.println("Compressed binary:\n" + code);
		System.out.println("Compressed binary is " + (h.toBinary(msg).length() / code.length()) + " times smaller than the original binary.");
		s.close();
	}
}

interface Queueable<E extends Comparable<E>> {
	public void enqueue(E obj);
	public E dequeue();
}

class HeapPriorityQueue implements Queueable<Node>{
	Node[] nodeList;
	HeapPriorityQueue() {
		nodeList = new Node[0];
	}

	public void enqueue(Node node) {
		Node[] newNodeList = new Node[nodeList.length + 1];
		if (nodeList.length == 0) {
			newNodeList[0] = node;
		} else {
			for (int i = 0; i < nodeList.length; i++) {
				newNodeList[i] = nodeList[i];
			}
			int addPos = newNodeList.length - 1;
			newNodeList[addPos] = node;
			while(newNodeList[addPos].compareTo(newNodeList[(int)((addPos - 1) / 2)]) < 0) {
				Node temp = newNodeList[addPos];
				newNodeList[addPos] = newNodeList[(int)((addPos - 1) / 2)];
		 		newNodeList[(int)((addPos - 1) / 2)] = temp;
				addPos--;
			}
		}
		nodeList = newNodeList;
	}
	
	public Node dequeue() {
		Node temp = nodeList[0];
		nodeList[0] = nodeList[nodeList.length - 1];
		nodeList[nodeList.length - 1] = temp;

		Node rtnVal = nodeList[nodeList.length - 1];

		nodeList[nodeList.length - 1] = null;

		Node[] newNodeList = new Node[nodeList.length - 1];
		int i = 0;
		while (nodeList[i] != null) {
			newNodeList[i] = nodeList[i];
			i++;
		}
		
		i = 0;
		while (true) {
			int l = 2*i+1;
			int r = 2*i+2;
			if (l >= newNodeList.length || r >= newNodeList.length) {
				break;
			}
			if (newNodeList[i].compareTo(newNodeList[l]) < 0 && newNodeList[i].compareTo(newNodeList[r]) < 0) {
				break;
			}

			if (newNodeList[r].compareTo(newNodeList[l]) < 0) {
				Node temp1 = newNodeList[i];
				newNodeList[i] = newNodeList[r];
				newNodeList[r] = temp1;
				i = r;
			} else {
				Node temp2 = newNodeList[i];
				newNodeList[i] = newNodeList[l];
				newNodeList[l] = temp2;
				i = l;
			}
		}

		nodeList = newNodeList;

		return rtnVal;
	}
}

class Node implements Comparable<Node> {

	String data;
	int freq;
	Node left;
	Node right;
	Node parent;

	Node(String data, int freq, Node left, Node right, Node parent) {
		this.data = data;
		this.freq = freq;
		this.left = left;
		this.right = right;
		this.parent = parent;
	}

	@Override
	public int compareTo(Node node) {
		return Integer.compare(this.freq, node.freq);
	}

}
