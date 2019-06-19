package compilador;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;


public class AnalisadorLexico extends Thread{
	File arquivo;
	LinkedBlockingQueue<Token> listaToken;
	private static final int LETRA = 0, E = 1, DIGITO = 2, ASPAS = 3, ABRE_CHAVE = 4, FECHA_CHAVE = 5, MAIOR = 6, MENOR = 7, IGUAL = 8, 
			SOMA = 9, SUBTRACAO = 10, MULTIPLICACAO = 11, DIVISAO = 12, ABRE_PARENTESES = 13, FECHA_PARENTESES = 14, PONTO_VIRGULA = 15, 
			PONTO = 16, BRANCO = 17, BARRA_INVERTIDA = 19, OUTRO_CARACTERE = 20;                
	static final int ESTADOS[][] = {
		
	//    L   E   D   "   {   }   >   <   =   +   -   *   /   (   )   ;   .  BR  EOF  \ OUT
		{12, 12,  1,  7,  5, -1, 18, 20, 20, 14, 15, 16, 17,  9, 10, 11,  2,  0, 00, -1, -1}, //inicio 					00
		{52,  3,  1, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52,  2, 52, 52, 52, -1}, //inteiro					01
		{53,  3,  2, -1, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, -1, 53, 53, 53, -1}, //real					02
		{-1, -1,  4, -1, -1, -1, -1, -1, -1,  4,  4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, //real com e (1)			03
		{-1, -1,  4, -1, 53, -1, 53, 53, 53, 53, 53, 53, 53, -1, -1, 53, -1, 53, 53, -1, -1}, //real com e com sinal    04
		{ 5,  5,  5,  5,  5,  6,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5}, //comentario inicio		05
		{54, 54, 54, 54, 54, -1, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, -1}, //comentario fim			06
		{ 7,  7,  7,  8,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7, -1,  7,  7}, //literal					07
		{55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, -1}, //literal fim				08
		{56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, -1}, //abre parenteses 	   	09
		{57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, -1}, //fecha parenteses       	10
		{58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, -1}, //ponto e virgula        	11
		{12, 12, 13, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, -1, 59, 59, -1, -1}, //identificador Letras	12
		{13, 13, 13, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, -1, 60, 60, -1, -1}, //identificador Digitos   13
		{61, 61, 61, 61, 61, 61, 61, 61, 61, 61, 61, 61, 61, 61, 61, 61, 61, 61, 61, 61, -1}, //soma 					14
		{62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, -1}, //subtração 				15
		{63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, -1}, //multiplicação           16
		{64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, -1}, //divisão					17
		{65, 65, 65, 65, 65, 65, 65, 65, 19, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, -1}, //maior 					18
		{66, 66, 66, 66, 66, 66, 66, 66, 66, 66, 66, 66, 66, 66, 66, 66, 66, 66, 66, 66, -1}, //maior ou igual 			19
		{67, 67, 67, 67, 67, 67, 23, 67, 21, 67, 24, 67, 67, 67, 67, 67, 67, 67, 67, 67, -1}, //menor 					20
		{68, 68, 68, 68, 68, 68, 68, 68, 68, 68, 68, 68, 68, 68, 68, 68, 68, 68, 68, 68, -1}, //menor ou igual 			21
		{69, 69, 69, 69, 69, 69, 69, 69, 69, 69, 69, 69, 69, 69, 69, 69, 69, 69, 69, 69, -1}, //igual 					22
		{70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, -1}, //diferente				23
		{71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, -1}, //atribuição				24
		
	//    L   E   D   "   {   }   >   <   =   +   -   *   /   (   )   ;   .  BR  EOF
	};
	
	public static final String[] TIPOS = {
			"Palavra Reservada", "NUM", "NUM", "Comentário", "Literal", "AB_P", "FC_P", "PT_V", "Id", "Id", 
			"OPM", "OPM", "OPM", "OPM", "OPR", "OPR", "OPR", "OPR", "OPR", "OPR", "RCB"
	};
	
	public AnalisadorLexico(File arquivo, LinkedBlockingQueue<Token> listaToken){
		this.arquivo = arquivo;
		this.listaToken = listaToken;
	}

	@Override
	public void run() {
		try{
			HashMap<Character, Integer> mapChar = new HashMap<>();
			mapChar.put('"', ASPAS);
			mapChar.put('{', ABRE_CHAVE);
			mapChar.put('}', FECHA_CHAVE);
			mapChar.put('>', MAIOR);
			mapChar.put('<', MENOR);
			mapChar.put('=', IGUAL);
			mapChar.put('+', SOMA);
			mapChar.put('-', SUBTRACAO);
			mapChar.put('*', MULTIPLICACAO);
			mapChar.put('/', DIVISAO);
			mapChar.put('(', ABRE_PARENTESES);
			mapChar.put(')', FECHA_PARENTESES);
			mapChar.put(';', PONTO_VIRGULA);
			mapChar.put('.', PONTO);
			mapChar.put(' ', BRANCO);
			mapChar.put('\t', BRANCO);
			mapChar.put('\n', BRANCO);
			mapChar.put('\\', BARRA_INVERTIDA);
			
			HashMap<String, Integer> mapChave = new HashMap<>();
			mapChave.put("inicio", 20);
			mapChave.put("fim", 21);
			mapChave.put("varinicio", 22);
			mapChave.put("varfim", 23);
			mapChave.put("se", 24);
			mapChave.put("entao", 25);
			mapChave.put("fimse", 26);
			mapChave.put("leia", 27);
			mapChave.put("escreva", 28);
			mapChave.put("inteiro", 29);
			mapChave.put("real", 30);
			mapChave.put("literal", 31);
			
			FileReader fr = new FileReader(arquivo);
			BufferedReader in = new BufferedReader(fr);
			
			int code = in.read(), estado = 0, tipo = 0, numLinha = 1, numColuna = 0;
			
			StringBuilder tokenBuilder = new StringBuilder();
			
			char c;
			int incrementar = 1; //define se deve incrementar os numeros de linha e coluna
			while(code != -1){
				numColuna+=incrementar;
				c = (char) code;
				//System.out.print(code + " ");
				if(Character.isAlphabetic(c)){ //identifica letras
					if('e' == c || 'E' == c) tipo = E;
					else tipo = LETRA;
				}else if(Character.isDigit(c)) tipo = DIGITO; //identifica números
				else if(c == '\r'){
					code = in.read(); //ignora o caractere \r do fim de linha do Windows
					continue;
				}else if(c == '\n'){ // incrementa o contador da linha e zera o contador de coluna
					numLinha+=incrementar;
					numColuna = 0;
					tipo = BRANCO;
				}else{ //reconhece outros caracteres
					Integer temp = mapChar.get(c);
					if(temp != null) tipo = temp; //identifica os caracteres especificados na linguagem
					else tipo = OUTRO_CARACTERE; //identifica os demais carateres
				}
				estado = ESTADOS[estado][tipo]; //pega o proximo estado
				incrementar = 1;
				if(estado > 0){
					if(estado < 50){ //se não é um fim de token, continua processando mais caracteres
						tokenBuilder.append(c);
						code = in.read();
					}else{ //se é um fim de token, volta para o estado 0 para reconhecer mais tokens
						int codTipo = estado - 52;
						String lexema = tokenBuilder.toString();
						if(estado == 59 && mapChave.get(lexema) != null) codTipo = mapChave.get(lexema); //caso encontre uma palavra reservada
						if(estado != 54){ //se o token nao for comentario						
							listaToken.offer(new Token(codTipo, lexema, numLinha, numColuna));
						}
						tokenBuilder = new StringBuilder();
						estado = 0;
						incrementar = 0;
					}
				}else if(estado == 0){
					code = in.read(); //ignora espaços em branco
				}else if(estado == -1){
					//informa a linha e coluna do erro e para o analisador
					System.out.printf("Erro na linha %d e coluna %d\n", numLinha, numColuna);
					System.exit(0);
				}
				
			}
			listaToken.offer(new Token(32, "", numLinha, numColuna));
			in.close();
		}catch(IOException e){
			e.printStackTrace();
		}

	}

}
