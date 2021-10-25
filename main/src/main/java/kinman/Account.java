package kinman;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    private String apiKey;

    public Account() {}

    public Account(String name, String apiKey) {
        this.name = name;
        this.apiKey = apiKey;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getApiKey() {
        return apiKey;
    }
}
