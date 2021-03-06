package com.pwnz.www.rockpapersword.model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.pwnz.www.rockpapersword.R;
import java.util.HashMap;

/**
 * This class will hold the pre-instantiated data used by board and ui.
 * This way we can share resources and reuse them while drawing without holding a unique reference
 * on each game object.
 */
public class GameStorage {
    private static final String highlighted = "_hl";

    public static final String KEY_P_ASHES = "player_ashes";
    public static final String KEY_P_SWORDMASTER = "player_swordmaster";
    public static final String KEY_P_KING = "player_king";
    public static final String KEY_P_STONE = "player_stone";
    public static final String KEY_P_PEPPER = "player_pepper";
    public static final String KEY_P_SHIELDON = "player_shieldon";

    public static final String KEY_P_ASHES_HL = KEY_P_ASHES + highlighted;
    public static final String KEY_P_SWORDMASTER_HL = KEY_P_SWORDMASTER + highlighted;
    public static final String KEY_P_KING_HL = KEY_P_KING + highlighted;
    public static final String KEY_P_STONE_HL = KEY_P_STONE + highlighted;
    public static final String KEY_P_PEPPER_HL = KEY_P_PEPPER + highlighted;
    public static final String KEY_P_SHIELDON_HL = KEY_P_SHIELDON + highlighted;

    public static final String KEY_E_DEFAULT = "enemy_soldier";
    public static final String KEY_E_ASHES = "enemy_ashes";
    public static final String KEY_E_SWORDMASTER = "enemy_swordmaster";
    public static final String KEY_E_KING = "enemy_king";
    public static final String KEY_E_STONE = "enemy_stone";
    public static final String KEY_E_PEPPER = "enemy_pepper";
    public static final String KEY_E_SHIELDON = "enemy_shieldon";

    public static final String SW_AS = "swordmaster_vs_ashes";
    public static final String SW_SH = "swordmaster_vs_shieldon";
    public static final String SW_ST = "swordmaster_vs_stone";
    public static final String SW_KI = "swordmaster_vs_king";
    public static final String SW_PE = "swordmaster_vs_pepper";
    public static final String SW_SW = "swordmaster_vs_swordmaster";

    public static final String PE_AS = "pepper_vs_ashes";
    public static final String PE_SH = "pepper_vs_shieldon";
    public static final String PE_ST = "pepper_vs_stone";
    public static final String PE_KI = "pepper_vs_king";
    public static final String PE_PE = "pepper_vs_pepper";
    public static final String PE_SW = "pepper_vs_swordmaster";

    public static final String KI_AS = "king_vs_ashes";
    public static final String KI_SH = "king_vs_shieldon";
    public static final String KI_ST = "king_vs_stone";
    public static final String KI_KI = "king_vs_king";
    public static final String KI_PE = "king_vs_pepper";
    public static final String KI_SW = "king_vs_swordmaster";

    public static final String ST_AS = "stone_vs_ashes";
    public static final String ST_SH = "stone_vs_shieldon";
    public static final String ST_ST = "stone_vs_stone";
    public static final String ST_KI = "stone_vs_king";
    public static final String ST_PE = "stone_vs_pepper";
    public static final String ST_SW = "stone_vs_swordmaster";

    public static final String SH_AS = "shieldon_vs_ashes";
    public static final String SH_SH = "shieldon_vs_shieldon";
    public static final String SH_ST = "shieldon_vs_stone";
    public static final String SH_KI = "shieldon_vs_king";
    public static final String SH_PE = "shieldon_vs_pepper";
    public static final String SH_SW = "shieldon_vs_swordmaster";

    public static final String AS_AS = "ashes_vs_ashes";
    public static final String AS_SH = "ashes_vs_shieldon";
    public static final String AS_ST = "ashes_vs_stone";
    public static final String AS_KI = "ashes_vs_king";
    public static final String AS_PE = "ashes_vs_pepper";
    public static final String AS_SW = "ashes_vs_swordmaster";

    public static HashMap<String, Bitmap> soldiersBitmapMap = new HashMap<>();

    public GameStorage(Resources resource) {
        initSoldiersBitmapMap(resource);
    }

    private void initSoldiersBitmapMap(Resources resource) {
        initPlayerSide(resource);
        initEnemySide(resource);
    }

    private void initEnemySide(Resources resource) {
        soldiersBitmapMap.put(KEY_E_DEFAULT, BitmapFactory.decodeResource(resource, R.drawable.soldier_enemy));
        soldiersBitmapMap.put(KEY_E_ASHES, BitmapFactory.decodeResource(resource, R.drawable.soldier_enemy_revealed_ashes));
        soldiersBitmapMap.put(KEY_E_SHIELDON, BitmapFactory.decodeResource(resource, R.drawable.soldier_enemy_revealed_shield));
        soldiersBitmapMap.put(KEY_E_SWORDMASTER, BitmapFactory.decodeResource(resource, R.drawable.soldier_enemy_revealed_sword));
        soldiersBitmapMap.put(KEY_E_KING, BitmapFactory.decodeResource(resource, R.drawable.soldier_enemy_revealed_king));
        soldiersBitmapMap.put(KEY_E_PEPPER, BitmapFactory.decodeResource(resource, R.drawable.soldier_enemy_revealed_paper));
        soldiersBitmapMap.put(KEY_E_STONE, BitmapFactory.decodeResource(resource, R.drawable.soldier_enemy_revealed_stone));
    }

    private void initPlayerSide(Resources resource) {
        soldiersBitmapMap.put(KEY_P_ASHES, BitmapFactory.decodeResource(resource, R.drawable.soldier_ashes));
        soldiersBitmapMap.put(KEY_P_ASHES_HL, BitmapFactory.decodeResource(resource, R.drawable.soldier_ashes_hl));
        soldiersBitmapMap.put(KEY_P_SHIELDON, BitmapFactory.decodeResource(resource, R.drawable.soldier_shieldon));
        soldiersBitmapMap.put(KEY_P_SHIELDON_HL, BitmapFactory.decodeResource(resource, R.drawable.soldier_shieldon_hl));
        soldiersBitmapMap.put(KEY_P_SWORDMASTER, BitmapFactory.decodeResource(resource, R.drawable.soldier_sword));
        soldiersBitmapMap.put(KEY_P_SWORDMASTER_HL, BitmapFactory.decodeResource(resource, R.drawable.soldier_sword_hl));
        soldiersBitmapMap.put(KEY_P_KING, BitmapFactory.decodeResource(resource, R.drawable.soldier_king));
        soldiersBitmapMap.put(KEY_P_KING_HL, BitmapFactory.decodeResource(resource, R.drawable.soldier_king_hl));
        soldiersBitmapMap.put(KEY_P_PEPPER, BitmapFactory.decodeResource(resource, R.drawable.soldier_paper));
        soldiersBitmapMap.put(KEY_P_PEPPER_HL, BitmapFactory.decodeResource(resource, R.drawable.soldier_paper_hl));
        soldiersBitmapMap.put(KEY_P_STONE, BitmapFactory.decodeResource(resource, R.drawable.soldier_stone));
        soldiersBitmapMap.put(KEY_P_STONE_HL, BitmapFactory.decodeResource(resource, R.drawable.soldier_stone_hl));
    }

    public static Bitmap getSoldierBitmapByType(SoldierType soldierType, int team, boolean highlighted) {

        String hl = highlighted ? "_hl" : "" ;
        switch (soldierType){
            case SHIELDON:      return soldiersBitmapMap.get( team == Board.TEAM_A ? KEY_E_SHIELDON + hl : KEY_P_SHIELDON + hl );
            case SWORDMASTER:   return soldiersBitmapMap.get( team == Board.TEAM_A ? KEY_E_SWORDMASTER + hl : KEY_P_SWORDMASTER + hl );
            case PEPPER:        return soldiersBitmapMap.get( team == Board.TEAM_A ? KEY_E_PEPPER + hl : KEY_P_PEPPER + hl );
            case KING:          return soldiersBitmapMap.get( team == Board.TEAM_A ? KEY_E_KING + hl : KEY_P_KING + hl );
            case STONE:         return soldiersBitmapMap.get( team == Board.TEAM_A ? KEY_E_STONE + hl : KEY_P_STONE + hl );
            case ASHES:         return soldiersBitmapMap.get( team == Board.TEAM_A ? KEY_E_ASHES + hl : KEY_P_ASHES + hl );
            case DEAULT:        return soldiersBitmapMap.get( team == Board.TEAM_A ? KEY_E_DEFAULT : KEY_P_KING );
        }

        return null;
    }
}
