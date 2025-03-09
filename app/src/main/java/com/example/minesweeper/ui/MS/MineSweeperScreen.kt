import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.minesweeper.R
import com.example.minesweeper.ui.MS.Cell
import com.example.minesweeper.ui.MS.MineSweeperViewModel
import com.example.minesweeper.ui.MS.Mode

@Composable
fun MineSweeperScreen (
    modifier: Modifier = Modifier,
    viewModel: MineSweeperViewModel = viewModel()
){
    var selectedDifficulty by remember { mutableStateOf<String?>("Easy")}
    val gameOver by remember { derivedStateOf { viewModel.isGameOver() } }
    val gameWin by remember { derivedStateOf { viewModel.isWin() } }
    val context = LocalContext.current

    Column (
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text(
            text = stringResource(R.string.minesweeper),
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6200EE),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .graphicsLayer {
                    scaleX = 1.2f
                    scaleY = 1.2f
                    shadowElevation = 8f
                    shape = RectangleShape
                    clip = true
                }
        )

        Button(
            onClick = {
                viewModel.currentMode = if (viewModel.currentMode == Mode.CHECK) Mode.FLAG else Mode.CHECK
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (viewModel.currentMode == Mode.FLAG) Color.Red else Color.Blue
            )
        ) {
            Text(
                text = if (viewModel.currentMode == Mode.FLAG) stringResource(R.string.flag_mode) else stringResource(R.string.check_mode),
                color = Color.White
            )
        }

        LaunchedEffect(gameOver, gameWin) {
            when {
                gameOver -> Toast.makeText(context,
                    context.getString(R.string.game_over), Toast.LENGTH_LONG).show()
                gameWin -> Toast.makeText(context,
                    context.getString(R.string.congratulations_you_won), Toast.LENGTH_LONG).show()
            }
        }

        MineSweeperBoard(
            onBoardCellClicked = { cell -> viewModel.onCellClicked(cell.row, cell.col) },
            board = viewModel.board)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.reset()
        }
        ){
            Text(text = stringResource(R.string.new_game))
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Yellow)
        ){Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.number_of, viewModel.mineCount()),
                fontSize = 20.sp,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = stringResource(
                    R.string.number_of_left,
                    viewModel.mineCount() - viewModel.flags
                ),
                fontSize = 20.sp,
                style = MaterialTheme.typography.labelMedium
            )
        }}

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Yellow)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                DifficultySelection(onDifficultySelected = { difficulty ->
                    selectedDifficulty = difficulty
                    viewModel.selectedDifficulty = difficulty
                    viewModel.reset()
                })

                Text(stringResource(R.string.selected_difficulty, selectedDifficulty.toString()))
            }
        }
    }
}


@Composable
fun DifficultySelection(onDifficultySelected: (String) -> Unit) {
    var selectedDifficulty by remember { mutableStateOf("Easy") }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.select_difficulty),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DifficultyButton(stringResource(R.string.easy), selectedDifficulty) { selectedDifficulty = it }
            DifficultyButton(stringResource(R.string.medium), selectedDifficulty) { selectedDifficulty = it }
            DifficultyButton(stringResource(R.string.hard), selectedDifficulty) { selectedDifficulty = it }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onDifficultySelected(selectedDifficulty)}) {
            Text(stringResource(R.string.confirm_selection))
        }
    }
}

@Composable
fun DifficultyButton(label: String, selectedDifficulty: String?, onDifficultySelected: (String) -> Unit){
    Button(
        onClick = { onDifficultySelected(label) },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selectedDifficulty == label) Color.Blue else Color.LightGray,
            contentColor = Color.White
        )
    ) {
        Text(label)
    }
}


@Composable
fun MineSweeperBoard(
    onBoardCellClicked: (Cell) -> Unit,
    board: Array<Array<Mode?>>,
    viewModel: MineSweeperViewModel = viewModel()
) {
    Canvas(modifier = Modifier
        .fillMaxWidth(0.8f)
        .aspectRatio(1f)
        .pointerInput(key1 = Unit)
        {
            detectTapGestures { offset ->
                Log.d("TAG", "TicTacToeScreen: ${offset.x}, ${offset.y} ")

                val row = (offset.y / (size.height / 9)).toInt()
                val col = (offset.x / (size.width / 9)).toInt()

                onBoardCellClicked(Cell(row = row, col = col))
            }
        }
    ) {
        val cellSize = size.width / 9

        // Draw the grid
        for (i in 0..9) {
            drawLine(
                color = Color.Black,
                start = Offset(i * cellSize, 0f),
                end = Offset(i * cellSize, size.height),
                strokeWidth = 2f
            )
            drawLine(
                color = Color.Black,
                start = Offset(0f, i * cellSize),
                end = Offset(size.width, i * cellSize),
                strokeWidth = 2f
            )
        }

        // Draw flags ("X") for flagged cells
        board.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, _ ->
                val mode = board[rowIndex][colIndex]
                if (mode == Mode.FLAG) {
                    val paintFlags = Paint().asFrameworkPaint().apply {
                        textSize = 40f
                        color = Color.Black.toArgb()
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    drawIntoCanvas { canvas ->
                        canvas.nativeCanvas.drawText("\uD83D\uDEA9", colIndex * cellSize + cellSize / 2, rowIndex * cellSize + cellSize / 2, paintFlags)
                    }
                }
                // Draw numbers of adjacent mines
                if (mode == Mode.CHECK) {
                    val text = viewModel.countAdjacentMines(rowIndex, colIndex).toString()
                    val x = colIndex * cellSize + cellSize / 2
                    val y = rowIndex * cellSize + cellSize / 2
                    val paintNoMines = Paint().asFrameworkPaint().apply {
                        textSize = 40f
                        color = Color.Black.toArgb()
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    val paintMines = Paint().asFrameworkPaint().apply {
                        textSize = 70f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    if (viewModel.isMine(rowIndex,colIndex)) {
                        drawIntoCanvas { canvas ->
                            canvas.nativeCanvas.drawText("💣", x, y , paintMines)
                            }
                    } else {
                        drawIntoCanvas { canvas ->
                            canvas.nativeCanvas.drawText(text,x ,y , paintNoMines)
                        }
                    }
                }
            }
        }
    }
}


