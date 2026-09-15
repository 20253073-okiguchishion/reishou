package com.swack.hcs.repository;

import com.swack.hcs.bean.UserData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

/**
 * ユーザー機能に関するDBアクセスを行う.
 */
@Repository
public class UserRepository {

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

}
