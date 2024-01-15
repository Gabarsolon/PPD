import java.util.ArrayDeque;
import java.util.Queue;

class Board{
    private int[][] array ;
    private int N ;
    int emptyRow;
    int emptyCol;
    boolean reached;
    int manhattan = 0;

    public Board(int[][] blocks){
        N = blocks.length ;
        array = new int[N][N] ;
        reached = true;
        for(int i = 0 ; i < N ; i++ ){
            for(int j = 0 ; j < N ; j++ ) {
                array[i][j] = blocks[i][j] ;
                if(array[i][j] == 0){
                    emptyRow = i;
                    emptyCol = j;
                }
                if(array[i][j] != N*i + j+1){
                    if(!(i==N-1 && j==N-1)){
                        reached = false;
                    }
                }
                int num = array[i][j] ;
                if(num==0){
                    continue ;
                }
                int indManhattan = Math.abs(Main.correctRow[num-1] - i)
                        + Math.abs(Main.correctCol[num-1]-j) ;
                manhattan += indManhattan ;
            }
        }
    }

    public int dimension(){
        return N ;
    }

    public int hamming(){
        int outOfPlace = 0 ;
        for(int i = 0 ; i < N ; i++ ) {
            for(int j = 0 ; j < N ; j++ ){
                if(i==N-1 && j==N-1) {
                    break ;
                }
                if(array[i][j] != i*N+j+1){
                    outOfPlace++ ;
                }
            }
        }
        return outOfPlace ;
    }

    public int manhattan(){
        return manhattan ;
    }

    public boolean isGoal(){
        return reached ;
    }

    public Board twin(){
        int[][] newArray = new int[N][N] ;
        for(int i = 0 ; i < N ; i++ ){
            for(int j = 0 ; j < N ; j++ ){
                newArray[i][j] = array[i][j] ;
            }
        }
        for(int i = 0 ; i < 2 ; i++ ) {
            if(newArray[i][0]==0 || newArray[i][1]==0){
                continue ;
            }
            int temp = newArray[i][0] ;
            newArray[i][0] = newArray[i][1] ;
            newArray[i][1] = temp ;
            break ;

        }
        return new Board(newArray) ;
    }

    public boolean equals(Object y){
        if(y==this){
            return true ;
        }
        if(y == null){
            return false ;
        }
        if(y.getClass() != this.getClass()){
            return false ;
        }
        Board that = (Board)y ;
        if(that.array.length != this.array.length){
            return false ;
        }
        for(int i = 0 ; i < N ; i++ ) {
            for(int j =  0 ; j < N ; j++ ) {
                if(that.array[i][j] != this.array[i][j] ){
                    return false ;
                }
            }
        }
        return true ;
    }

    public Iterable<Board> neighbors(){
        Queue<Board> q = new ArrayDeque<Board>() ;
        int firstIndex0 = 0 ;
        int secondIndex0 = 0 ;
        for(int i = 0 ; i < N ; i++ ){
            for(int j = 0 ; j < N ; j++ ) {
                if(array[i][j] == 0){
                    firstIndex0 = i ;
                    secondIndex0 = j ;
                    break ;
                }
            }
        }
        if(secondIndex0-1>-1){
            int[][] newArr = getCopy() ;
            exch(newArr,firstIndex0,secondIndex0,firstIndex0,secondIndex0-1) ;
            q.add(new Board(newArr)) ;
        }
        if(secondIndex0+1<N){
            int[][] newArr = getCopy() ;
            exch(newArr,firstIndex0,secondIndex0,firstIndex0,secondIndex0+1) ;
            q.add(new Board(newArr)) ;
        }
        if(firstIndex0-1>-1){
            int[][] newArr = getCopy() ;
            exch(newArr,firstIndex0,secondIndex0,firstIndex0-1,secondIndex0) ;
            q.add(new Board(newArr)) ;
        }
        if(firstIndex0+1<N){
            int[][] newArr = getCopy() ;
            exch(newArr,firstIndex0,secondIndex0,firstIndex0+1,secondIndex0) ;
            q.add(new Board(newArr)) ;
        }
        return q ;
    }

    private int[][] getCopy(){
        int[][] copy = new int[N][N] ;
        for(int i = 0 ; i < N ; i++ ) {
            for(int j = 0 ; j < N ; j++ ){
                copy[i][j] = array[i][j] ;
            }
        }
        return copy ;
    }

    private void exch(int[][] arr, int firstIndex,int secIndex,int firstIndex2,int secIndex2){
        int temp = arr[firstIndex][secIndex] ;
        arr[firstIndex][secIndex] = arr[firstIndex2][secIndex2] ;
        arr[firstIndex2][secIndex2] = temp ;
    }

    public String toString(){
        StringBuilder s = new StringBuilder() ;
        s.append(N + "\n") ;
        for(int i = 0 ; i < N ; i++ ){
            for(int j = 0 ; j  < N ; j++ ) {
                s.append(String.format("%4d",array[i][j])) ;
            }
            s.append("\n") ;
        }
        return s.toString() ;
    }
}