package it.luciorizzi.scacchi.util;

import it.luciorizzi.scacchi.model.lobby.LobbyProperties;
import it.luciorizzi.scacchi.model.lobby.Player;
import it.luciorizzi.scacchi.model.movement.Move;
import it.luciorizzi.scacchi.model.movement.MoveSet;
import it.luciorizzi.scacchi.model.movement.MoveType;
import it.luciorizzi.scacchi.model.movement.Position;
import it.luciorizzi.scacchi.model.type.PieceColor;
import it.luciorizzi.scacchi.openapi.model.*;
import it.luciorizzi.scacchi.openapi.model.MoveDTO.*;

import java.util.ArrayList;
import java.util.List;

public class ApiDTOConverter {
    public static Position toPosition(PositionDTO positionDTO) {
        return new Position(positionDTO.getRow(), positionDTO.getCol());
    }

    public static PositionDTO toPositionDTO(Position position) {
        return new PositionDTO().row(position.row()).col(position.column());
    }

    public static MoveType toMoveType(MoveTypeEnum moveTypeEnum) {
        return MoveType.valueOf(moveTypeEnum.name());
    }

    public static Move toMove(MoveDTO moveDTO) {
        return new Move(toPosition(moveDTO.getOrigin()), toPosition(moveDTO.getDestination()), toMoveType(moveDTO.getMoveType()), moveDTO.getPromotion().getValue().charAt(0));
    }

    public static MoveDTO toMoveDTO(Move move) {
        MoveDTO result = new MoveDTO()
                .origin(toPositionDTO(move.getOrigin()))
                .destination(toPositionDTO(move.getDestination()))
                .moveType(MoveTypeEnum.valueOf(move.getMoveType().name()));
        if (move.getPromotion() != null) {
            result.promotion(PromotionEnum.fromValue(String.valueOf(move.getPromotion())));
        }
        return result;
    }

    public static List<MoveDTO> toListOfMoves(MoveSet possibleMoves) {
        List<MoveDTO> result = new ArrayList<>();
        for (Move move : possibleMoves.getMoves()) {
            result.add(toMoveDTO(move));
        }
        return result;
    }

    public static ColorEnum toColorEnum(PieceColor color) {
        return ColorEnum.valueOf(color.name());
    }

    public static LobbyPropertiesDTO toLobbyPropertiesDTO(LobbyProperties properties) {
        return new LobbyPropertiesDTO()
                .isPrivate(properties.isPrivate())
                .isRated(properties.isRated())
                .isTimed(properties.isTimed())
                .timeSeconds( (int) properties.getTimeSeconds() )
                .incrementSeconds( (int) properties.getIncrementSeconds() );
    }

    public static PlayerDTO toPlayerDTO(Player player) {
        if (player == null) {
            return null;
        }
        return new PlayerDTO()
                .token(null)
                .name( player.getName() )
                .color( toColorEnum(player.getColor()) );
    }

    public static PieceColor toPieceColor(ColorEnum playerOneColor) {
        return PieceColor.valueOf(playerOneColor.name());
    }
}