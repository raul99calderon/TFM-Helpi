package es.upm.miw.helpiforstaff.daos;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.upm.miw.helpiforstaff.models.RankingUserDto;

public final class RankingDao {
    private static DatabaseReference rankingRef;
    private static final String RANKING = "ranking";
    private static RankingDao instance;
    private static final String EMAIL = "email";

    public static RankingDao getInstance() {
        if (instance == null) {
            instance = new RankingDao();
            rankingRef = FirebaseDatabase.getInstance().getReference(RANKING);
        }

        return instance;
    }

    public interface OnGetRankingUser {
        void onGetRankingUser(RankingUserDto rankingUserDto, String rankingUserKey);
    }

    public void getRankingUser(String email, OnGetRankingUser onGetRankingUser) {
        rankingRef.orderByChild(EMAIL)
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        RankingUserDto rankingUserDto = null;
                        String rankingUserKey = null;
                        for (DataSnapshot k : snapshot.getChildren()) {
                            rankingUserDto = k.getValue(RankingUserDto.class);
                            rankingUserKey = k.getKey();
                        }

                        onGetRankingUser.onGetRankingUser(rankingUserDto, rankingUserKey);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public interface OnSetRankingUser {
        void onSetRankingUser();
        void onError();
    }

    public void setRankingUser(RankingUserDto rankingUserDto, String rankingUserKey, OnSetRankingUser onSetRankingUser) {
        rankingRef.child(rankingUserKey)
                .setValue(rankingUserDto)
                .addOnSuccessListener(unused -> onSetRankingUser.onSetRankingUser())
                .addOnFailureListener(e -> onSetRankingUser.onError());
    }
}
