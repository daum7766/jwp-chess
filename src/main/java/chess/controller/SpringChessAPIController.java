package chess.controller;

import chess.domain.Room;
import chess.service.ChessService;
import dto.ChessGameDto;
import dto.MoveDto;
import dto.RoomDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SpringChessAPIController {
    @Autowired
    private ChessService chessService;

    @GetMapping("/rooms")
    public ResponseEntity<List<RoomDto>> loadAllRoom() {
        return chessService.loadAllRoom();
    }

    @PostMapping("/rooms/{id}")
    public ResponseEntity<ChessGameDto> loadGame(@PathVariable("id") Long roodId, @RequestBody Room room) {
        return chessService.loadGame(roodId, room);
    }

    @PostMapping("/rooms")
    public void createRoom(@RequestBody Room room) {
        chessService.createRoom(room);
    }

    @PutMapping("/room/{id}/pieces")
    public ResponseEntity<ChessGameDto> movePiece(@PathVariable("id") Long roodId, @RequestBody MoveDto moveDto) {
        return chessService.movePiece(roodId, moveDto);
    }
}
