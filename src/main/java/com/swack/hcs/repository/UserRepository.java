package com.swack.hcs.repository;

import com.swack.hcs.bean.UserData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

/**
 * ユーザー機能に関するDBアクセスを行う.
 */
@Repository
public class UserRepository {

  @Autowired
  private JdbcTemplate unNamedJdbc;

  @Autowired
  private NamedParameterJdbcTemplate jdbc;

  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   * ログイン判定を実行.
   *
   * @param mailAddress メールアドレス
   * @param password    パスワード
   * @return ユーザーデータ（ログイン失敗時や、存在しない場合はnull）
   */
  public UserData login(String mailAddress, String password) {
    /** SQL ログインチェック */
    final String SQL_LOGIN = "SELECT PASSWORD FROM USERS WHERE MAILADDRESS = :mailAddress AND ENABLED = 'Y'";

    // パラメータを格納するためのマップを作成
    Map<String, Object> params = new HashMap<>();
    params.put("mailAddress", mailAddress);

    // データベースのクエリを実行し、結果を取得
    List<Map<String, Object>> resultList = jdbc.queryForList(SQL_LOGIN, params);

    if (resultList.size() != 1) {
      // ユーザーが存在しない場合
      return null;
    }

    // 入力されたパスワードとデータベースに保存されているハッシュを比較
    String storedPasswordHash = (String) resultList.get(0).get("PASSWORD");
    if (!passwordEncoder.matches(password, storedPasswordHash)) {
      // パスワードが一致しない場合
      return null;
    }

    // ユーザーが存在する場合
    UserData userData = selectOne(mailAddress);

    updateLoginedAt(userData.userId());

    return userData;
  }

  /**
   * メールアドレスに基づいたユーザー情報取得.
   *
   * @param mailAddress メールアドレス
   * @return ユーザー情報
   */
  public UserData selectOne(String mailAddress) {
    final String sql = "SELECT USERID, USERNAME, USERIMGPATH, ROLE FROM USERS WHERE MAILADDRESS = :mailAddress";

    Map<String, Object> params = new HashMap<>();
    params.put("mailAddress", mailAddress);

    List<Map<String, Object>> resultList = jdbc.queryForList(sql, params);

    UserData user = null;
    for (Map<String, Object> map : resultList) {
      String userId = (String) map.get("USERID");
      String userName = (String) map.get("USERNAME");
      String userImgPath = (String) map.get("USERIMGPATH");
      String role = (String) map.get("ROLE");
      user = new UserData(userId, userName, mailAddress, "********", userImgPath, role);
    }

    return user;
  }

  /**
   * ユーザーの登録
   *
   * @param userName    氏名
   * @param mailAddress メールアドレス
   * @param password    パスワード
   *
   * @return 更新された行数
   */
  public int save(String userId, String userName, String mailAddress, String password) {
    final String SQL_INSERT_ONE = "INSERT INTO users (userid, username, mailaddress, password, role, enabled) VALUES (:userId, :userName, :mailAddress, :password, 'USER', 'Y')";

    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);
    params.put("userName", userName);
    params.put("mailAddress", mailAddress);
    String passwordHash = passwordEncoder.encode(password);
    params.put("password", passwordHash);

    int updateRow = jdbc.update(SQL_INSERT_ONE, params);

    return updateRow;
  }

  /**
   * ユーザー新規登録時の、登録済であるか確認
   *
   * @param userName    登録時に入力されたユーザー名
   * @param mailAddress 登録時に入力されたメールアドレス
   *
   * @return SQLを実行したときに取得された行数(0 or 1)
   */
  public int userDupticalCheck(String userName, String mailAddress) {
    final String SQL_DUPTICAL_CHECK = "SELECT username, mailaddress FROM users WHERE username = :userName AND mailaddress = :mailAddress";

    Map<String, Object> params = new HashMap<>();
    params.put("userName", userName);
    params.put("mailAddress", mailAddress);

    List<Map<String, Object>> resultList = jdbc.queryForList(SQL_DUPTICAL_CHECK, params);

    if (resultList.size() == 0) {
      // クエリの結果が０件
      return 0;
    }

    // クエリの結果が１件
    return 1;
  }

  /**
   * 指定のユーザーのログイン状態を保持
   *
   * @param userId ユーザーID
   *
   * @return 更新された行数
   */
  public int loginStore(String userId) {
    final String SQL = "UPDATE users SET loginstored = TRUE WHERE userid = :userId";

    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);

    int updateRow = jdbc.update(SQL, params);

    return updateRow;
  }

  /**
   * ログイン状態が保持されているか検証
   *
   * @return 保持されていればtrue、保持されていなければfalse
   */
  public boolean isLoginStored() {
    final String SQL = "SELECT mailaddress, password FROM users WHERE loginstored = TRUE";

    List<Map<String, Object>> resultList = unNamedJdbc.queryForList(SQL);

    if (resultList.size() == 0) {
      // クエリの結果が０件（保持されていない）
      return false;
    }

    // クエリの結果が１件（保持されている）
    return true;

  }

  public String selectMaxUserId() {
    final String SQL = "SELECT MAX(userid) AS maxUserId FROM users";

    List<Map<String, Object>> resultList = unNamedJdbc.queryForList(SQL);

    if (resultList.size() == 0) {
      // クエリの結果が０件
      return null;
    }

    // クエリの結果が１件
    String maxUserId = (String) resultList.get(0).get("maxUserId");

    return maxUserId;
  }

  public int updateLoginedAt(String userId) {
    final String SQL = "UPDATE users SET logined_at = CURRENT_TIMESTAMP WHERE userid = :userId";

    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);

    int updateRow = jdbc.update(SQL, params);

    return updateRow;
  }

  /**
   * 指定されたユーザーIDに対応するユーザー名を取得する.
   *
   * @param userId ユーザーID
   * @return ユーザー名
   */
  public String selectUserName(String userId) {
    final String SQL = "SELECT USERNAME FROM USERS WHERE USERID = :userId";
    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);
    return jdbc.queryForObject(SQL, params, String.class);
  }

  /**
   * 指定されたチャットログID、ユーザーIDに対応するメッセージを更新する.
   *
   * @param chatLogId チャットログID
   * @param userId    ユーザーID
   * @param message   メッセージ
   * @return 更新された行数
   */
  public int updateMessage(Integer chatLogId, String userId, String message) {
    final String SQL = "UPDATE chatlog SET message = :message WHERE chatlogid = :chatLogId AND userid = :userId";

    Map<String, Object> params = new HashMap<>();
    params.put("chatLogId", chatLogId);
    params.put("userId", userId);
    params.put("message", message);

    int updateRow = jdbc.update(SQL, params);

    return updateRow;
  }
}
