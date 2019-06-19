package compilador;

public class Token {
	private int codigo;
	private String lexema;
	private int linha, coluna;
	//										  0  1   2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20
	private static final int codigoSint[] = { 1, 1, -1, 2, 6, 7, 8, 0, 0, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 5, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 37};
	
	public Token(int codigoLexico, String valor, int linha, int coluna) {
		this.codigo = codigoSint[codigoLexico];
		this.lexema = valor;
		this.linha = linha;
		this.coluna = coluna;
	}

	public int getCodigo() {
		return codigo;
	}

	public String getValor() {
		return lexema;
	}

	public int getLinha() {
		return linha;
	}

	public int getColuna() {
		return coluna;
	}
	
	@Override
	public String toString() {
		return lexema;
	}
}
