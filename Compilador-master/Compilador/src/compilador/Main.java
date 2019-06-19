package compilador;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
	
	public static void main(String[] args){
		LinkedBlockingQueue<Token> listaToken = new LinkedBlockingQueue<>();
		new AnalisadorLexico(new File("fonte.alg"), listaToken).start();
		new AnalisadorSintatico(listaToken).start();
	}
}
