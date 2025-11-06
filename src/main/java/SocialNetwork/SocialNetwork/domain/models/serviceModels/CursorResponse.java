package SocialNetwork.SocialNetwork.domain.models.serviceModels;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CursorResponse <T>{
    private List<T> data;
    private LocalDateTime nextCursor;
}
