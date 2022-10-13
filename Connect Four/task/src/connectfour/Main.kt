package connectfour

fun main() {
    var check = false
    println("Connect Four")
    println("First player's name:")
    val firstPlayer = readln()
    println("Second player's name:")
    val secondPlayer = readln()
    while (!check) {
        println("Set the board dimensions (Rows x Columns)")
        println("Press Enter for default (6 x 7)")
        var dimensions = readln().lowercase()
        dimensions = defaultDimension(dimension = dimensions)
        dimensions = processDimension(dimension = dimensions)
        val checkFail = correctDimensions(filteredDimension = dimensions)
        val score = Score()
        var numOfGames = 0
        if (!checkFail.failed) {
            do {
               numOfGames  = processNumOfGames(checkNumberOfGames())
            } while (numOfGames < 1)
            check = true
            val board = Board(dimension = dimensions)
            println("$firstPlayer VS $secondPlayer")
            println("${board.row} X ${board.col} board")
            if (numOfGames == 1)
                println("Single game")
            else
                println("Total $numOfGames games")
            repeat(numOfGames) {
                if(numOfGames != 1)
                    println("Game #${it + 1}")
                if(it > 0)
                    board.resetBoard()
                board.drawBoard()
                playGame(board, firstPlayer, secondPlayer, score)
                println("Score")
                println("$firstPlayer: ${score.firstPlayer} $secondPlayer: ${score.secondPlayer}")
                if(numOfGames -1 == it){
                    println("Game over!")
                }
            }
        } else {
            if (checkFail.multiply)
                println("Invalid input")
            else if (checkFail.row)
                println("Board rows should be from 5 to 9")
            else if (checkFail.col)
                println("Board columns should be from 5 to 9")
        }
    }
}

fun defaultDimension(dimension: String): String{
    if (dimension.isEmpty()) {
        return "6 x 7"
    }
    return dimension
}

fun processDimension(dimension: String): String {
    return dimension.filter { !it.isWhitespace()}
}

fun correctDimensions(filteredDimension: String): Fail {
    if (
        !filteredDimension.matches(Regex("^[5-9]'x'[5-9]$"))) {
        if(filteredDimension.contains('x')) {
            val row = processRow(dimension = filteredDimension)
            val col = processCol(dimension = filteredDimension)
            val multiply = processMultiply(dimension = filteredDimension)
            if (filteredDimension.length < 3 || checkAlpha(row) || checkAlpha(col)
            ) {
                return Fail(failed = true, row = false, col = false, multiply = true)
            } else if (multiply != "x"
            ) {
                return Fail(failed = true, row = false, col = false, multiply = true)
            } else if (row.toIntOrNull() !in 5..9
            ) {
                return Fail(failed = true, row = true, col = false, multiply = false)
            } else if (col.toIntOrNull() !in 5..9
            ) {
                return Fail(failed = true, row = false, col = true, multiply = false)
            }
        }
        else
            return Fail(failed = true, row = false, col = false, multiply = true)
    }
    return Fail(failed = false, row = false, col = false, multiply = false)
}

fun checkAlpha(str: String): Boolean{
    return str.matches(".*[a-zA-Z]+.*".toRegex())
}

fun processRow(dimension: String): String {
    return dimension.split('x')[0]
}

fun processCol(dimension: String): String {
    return dimension.split('x')[1]
}

fun processMultiply(dimension: String): String {
    return dimension[dimension.indexOf('x')].toString()
}

class Board(val dimension: String) {
    val row = dimension.first().toString().toInt()
    val col = dimension.last().toString().toInt()
    private var board = Array(row) { Array(col) { "" } }
    var currentPlayer = 0

    fun drawBoard(){
        print(" ")
        for(colNum in 1..this.col){
            print("$colNum ")
        }
        println()
        for(row in 1..this.row){
            for(col in 1..this.col+1){
                if(col == this.col+1)
                    print("| ")
                else{
                    if(this.board[row-1][col-1] != "")
                        print("|${this.board[row-1][col-1]}")
                    else
                        print("| ")
                }
            }
            println()
        }
        for(equals in 1 until (this.col*2+2)){
            print("=")
        }
        println()
    }

    private fun isColFull(markedCol: Int): Boolean{
        var count = 0
        for(rowIndex in 1  .. this.row){
            if(this.board[rowIndex-1][markedCol-1] == ""){
                count++
            }
        }
        if(count == 0){
            return true
        }
        return false
    }

    fun playTurn(mark: String, markedCol: Int) : Boolean{
        if(!isColFull(markedCol)) {
            for (rowIndex in (this.row - 1) downTo 0) {
                if (this.board[rowIndex][markedCol-1] == "") {
                    this.board[rowIndex][markedCol-1] = mark
                    drawBoard()
                    return true
                }
            }
        }
        else{
            println("Column $markedCol is full")
            return false
        }
        return false
    }

    fun checkWinningCondition(mark: String): Boolean{
        //Check horizontal locations for win
        for (c in 0 until this.col-3)
            for (r in 0 until this.row)
                if (this.board[r][c] == mark && this.board[r][c+1] == mark && this.board[r][c+2] == mark && this.board[r][c+3] == mark)
                    return true

        //Check vertical locations for win
        for (c in 0 until this.col)
            for (r in 0 until this.row-3)
                if (this.board[r][c] == mark && this.board[r+1][c] == mark && this.board[r+2][c] == mark && this.board[r+3][c] == mark)
                    return true

        //Check positively sloped diagonal
        for (c in 0 until this.col-3)
            for (r in 0 until this.row-3)
                if (this.board[r][c] == mark && this.board[r+1][c+1] == mark && this.board[r+2][c+2] == mark && this.board[r+3][c+3] == mark)
                    return true

        //Check negatively sloped diagonal
        for (c in 0 until this.col-3)
            for (r in 3 until this.row)
                if (this.board[r][c] == mark && this.board[r-1][c+1] == mark && this.board[r-2][c+2] == mark && this.board[r-3][c+3] == mark)
                    return true

        return false
    }

    fun checkBoardFull(): Boolean {
        for (c in 0 until this.col)
            for (r in 0 until this.row)
                if (this.board[r][c] == "")
                    return false
        return true
    }

    fun resetBoard(){
        this.board = Array(row) { Array(col) { "" } }
    }
}


fun playGame(board: Board, firstPlayer: String, secondPlayer: String, score: Score){
    var playerInput : String
    val firstPlayerSign = "o"
    val secondPlayerSign = "*"
    var count = 1
    var win = false
    do {
        if(board.currentPlayer == 1){
            println("$secondPlayer's turn:")
        }
        else if(board.currentPlayer == 2 || board.currentPlayer == 0){
            println("$firstPlayer's turn:")
        }
        playerInput = readln()
        if(playerInput.lowercase() == "end")
            println("Game over!")
        else if(checkAlpha(playerInput) || playerInput == "")
            println("Incorrect column number")
        else if(playerInput.toIntOrNull() !in 1..board.col)
            println("The column number is out of range (1 - ${board.col})")
        else{
            var result: Boolean = false
            if((board.currentPlayer == 1)){
                board.currentPlayer = 2
                result = board.playTurn(secondPlayerSign, playerInput.toInt())
                win = determineWin(board, secondPlayerSign, secondPlayer, 2, score)
            } else if(board.currentPlayer == 2 || board.currentPlayer == 0){
                board.currentPlayer = 1
                result = board.playTurn(firstPlayerSign, playerInput.toInt())
                win = determineWin(board, firstPlayerSign, firstPlayer, 1, score)
            }
            if(result)
                count++
        }
    } while (playerInput.lowercase() != "end" && !win)
}

fun determineWin(board: Board, mark: String, playerName: String, player: Int, score: Score): Boolean {
    if(board.checkWinningCondition(mark)){
        println("Player $playerName won")
        //println("Game Over!")
        if(player == 1){
            score.firstPlayer+=2
        }
        else{
            score.secondPlayer+=2
        }
        return true
    }
    else{
        if(board.checkBoardFull()){
            println("It is a draw")
            score.firstPlayer+=1
            score.secondPlayer+=1
            return true
        }
    }
    return false
}

fun checkNumberOfGames(): String {
    println("Do you want to play single or multiple games?")
    println("For a single game, input 1 or press Enter")
    println("Input a number of games:")
    return readln()
}

fun processNumOfGames(numOfGames: String): Int {
    return if(checkAlpha(numOfGames) || numOfGames == "0") {
        println("Invalid Input")
        0
    }
    else if(numOfGames == "")
        1
    else
        numOfGames.toInt()
}

class Fail(val failed: Boolean, val row: Boolean, val col: Boolean, val multiply: Boolean)

class Score(var firstPlayer: Int = 0, var secondPlayer: Int = 0)


