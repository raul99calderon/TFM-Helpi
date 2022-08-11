package es.upm.miw.helpi.daos;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.upm.miw.helpi.models.RankingUser;
import es.upm.miw.helpi.models.RankingUserDto;

public final class RankingDao {

    private static DatabaseReference rankingRef;
    private static final String RANKING = "ranking";
    private static final String EMAIL = "email";
    private static RankingDao instance;
    private static final String ATTENDED_EVENTS = "numAttendedEvents";
    private static final Integer LIMIT = 10;

    public interface OnCreateRanking {
        void created();
        void errorCreating();
    }

    public interface OnGetRanking {
        void getRanking(List<RankingUser> rankingUsers);
    }

    public static RankingDao getInstance() {
        if (instance == null) {
            instance = new RankingDao();
            rankingRef = FirebaseDatabase.getInstance().getReference(RANKING);
        }

        return instance;
    }

    public void createRankingUser(String email, OnCreateRanking onCreateRanking) {
        rankingRef.push()
                .setValue(new RankingUserDto(email, 0))
                .addOnSuccessListener(unused -> onCreateRanking.created())
                .addOnFailureListener(e -> onCreateRanking.errorCreating());
    }

    public void getRanking(OnGetRanking onGetRanking) {
        rankingRef.orderByChild(ATTENDED_EVENTS)
                .limitToLast(LIMIT)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<RankingUser> rankingUsers = new ArrayList<>();
                        for (DataSnapshot actual : snapshot.getChildren()) {
                            RankingUserDto rankingUserDto = actual.getValue(RankingUserDto.class);
                            RankingUser rankingUser = new RankingUser(Objects.requireNonNull(rankingUserDto));
                            rankingUsers.add(rankingUser);
                        }
                        onGetRanking.getRanking(rankingUsers);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public interface OnDeleteUserRanking {
        void onDeleted();
    }

    public void deleteUserRanking(String email, OnDeleteUserRanking onDeleteUserRanking) {
        rankingRef.orderByChild(EMAIL)
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot k : snapshot.getChildren()) {
                                k.getRef().removeValue();
                            }
                        }
                        onDeleteUserRanking.onDeleted();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
