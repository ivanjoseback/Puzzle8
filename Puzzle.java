package trabalho;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
/**
 *
 * @author Ivan
 */
public class Puzzle {

    
    static final byte [] objetivoPuzzle = { 1, 2, 3, 4, 5, 6, 7, 8, 0 };

    //fila de prioridades
    final PriorityQueue <estado> aberto = new PriorityQueue<estado>(100, new Comparator<estado>() {
        @Override
        public int compare(estado a, estado b) { 
            return a.prioridade()- b.prioridade();
        }
    });

    final HashSet <estado> fechado = new HashSet <estado>();
    
    class estado {
        final byte [] puzzle;    
        final int spaceIndex;   
        final int g;            
        final int h;            
        final estado prev;       

        
        int prioridade() {
            return g + h;
        }

        //espaço do índice
        estado(byte [] inicial) {
            puzzle = inicial;
            spaceIndex = index(puzzle, 0);
            g = 0;
            h = heuristic(puzzle);
            prev = null;
        }

        //deslize do índice
        estado(estado prev, int slideFromIndex) {
            puzzle = Arrays.copyOf(prev.puzzle, prev.puzzle.length);
            puzzle[prev.spaceIndex] = puzzle[slideFromIndex];
            puzzle[slideFromIndex] = 0;
            spaceIndex = slideFromIndex;
            g = prev.g + 1;
            h = heuristic(puzzle);
            this.prev = prev;
        }

        
        boolean objetivo() {
            return Arrays.equals(puzzle, objetivoPuzzle);
        }

        
        estado moveS() { return spaceIndex > 2 ? new estado(this, spaceIndex - 3) : null; }       
        estado moveN() { return spaceIndex < 6 ? new estado(this, spaceIndex + 3) : null; }       
        estado moveE() { return spaceIndex % 3 > 0 ? new estado(this, spaceIndex - 1) : null; }       
        estado moveW() { return spaceIndex % 3 < 2 ? new estado(this, spaceIndex + 1) : null; }

        
        void print() {
            System.out.println("p = " + prioridade() + " = g+h = " + g + "+" + h);
            for (int i = 0; i < 9; i += 3)
                System.out.println(puzzle[i] + " " + puzzle[i+1] + " " + puzzle[i+2]);
        }

        
        void imprimeTodas() {
            if (prev != null) prev.imprimeTodas();
            System.out.println();
            print();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof estado) {
                estado other = (estado)obj;
                return Arrays.equals(puzzle, other.puzzle);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(puzzle);
        }
    }

    
    void addSuccessor(estado sucessor) {
        if (sucessor != null && !fechado.contains(sucessor)) 
            aberto.add(sucessor);
    }

    
    void resolve(byte [] inicial) {

        aberto.clear();
        fechado.clear();

    
        long comeco = System.currentTimeMillis();

    
        aberto.add(new estado(inicial));
//vazio
        while (!aberto.isEmpty()) {

    
            estado estado = aberto.poll();

            
            if (estado.objetivo()) {
                long tempo = System.currentTimeMillis() - comeco;
                estado.imprimeTodas();
                System.out.println("Tempo (ms) = " + tempo);
                return;
            }

            
            fechado.add(estado);

            
            addSuccessor(estado.moveS());
            addSuccessor(estado.moveN());
            addSuccessor(estado.moveW());
            addSuccessor(estado.moveE());
        }
    }

    
    static int index(byte [] a, int val) {
        for (int i = 0; i < a.length; i++)
            if (a[i] == val) return i;
        return -1;
    }

    
    static int manhattenDistance(int a, int b) {
        return Math.abs(a / 3 - b / 3) + Math.abs(a % 3 - b % 3);
    }

    
    static int heuristic(byte [] puzzle) {
        int h = 0;
        for (int i = 0; i < puzzle.length; i++)
            if (puzzle[i] != 0)
                h += manhattenDistance(i, puzzle[i]);
        return h;
    }

    public static void main(String[] args) {

        
        byte [] inicial = { 5, 2, 0, 3, 4, 6, 8, 1, 7 };

        
        new Puzzle().resolve(inicial);
    }
}