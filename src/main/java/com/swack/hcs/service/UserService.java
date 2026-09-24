package com.swack.hcs.service;

import com.swack.hcs.bean.UserData;
import com.swack.hcs.repository.UserRepository;
import com.swack.hcs.util.AppConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ユーザー機能を実行するサービスクラス.
 * ユーザーの登録、情報の取得、パスワードの更新などを行います。
 */
@Transactional
@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  /**
   * 指定されたメールアドレスに基づいたユーザー情報取得.
   *
   * @param mailAddress ユーザーのメールアドレス
   * @return 対応するユーザー情報。存在しない場合はnullを返します。
   */
  public UserData getUser(String mailAddress) {
    return userRepository.selectOne(mailAddress);
  }

  /**
   * ユーザーの新規登録
   *
   * @param userName 氏名
   * @param mailAddress メールアドレス
   * @param password パスワード
   *
   * @return 登録に成功した場合はtrue、失敗した場合はfalse
   */
  public boolean insert(String userName, String mailAddress, String password) {
    String userId = createUserId();
    int updateRow = userRepository.save(userId, userName, mailAddress, password);

    if (updateRow != AppConstants.EXPECTED_UPDATE_COUNT) {
      return false;
    }

    return true;
  }

  /**
   * ユーザーの重複チェック
   *
   * @param userName 氏名
   * @param mailAddress メールアドレス
   *
   * @return 重複があればtrue、なければfalse
   */
  public boolean check(String userName, String mailAddress) {
    int rows = userRepository.userDupticalCheck(userName, mailAddress);

    if (rows == 1) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * 指定のユーザーのログイン状態を保持
   *
   * @param userId ユーザーID
   *
   * @return 成功可否
   */
  public boolean loginStore(String userId) {
    int rows = userRepository.loginStore(userId);

    if (rows == 1) {
      return true;
    } else {
      return false;
    }
  }

  /** ユーザーIDのプレフィックス */
  private static final String PREFIX_USER_ID = "U";
  /** ユーザーIDの開始番号 */
  private static final String START_NO = "0001";

  /**
   * 新しいユーザーIDの生成.
   *
   * @return 生成されたユーザーID
   */
  String createUserId() {
    // 最大のユーザーIDを取得する（新規登録画面②のSQL）
    String maxUserId = userRepository.selectMaxUserId();
    if (maxUserId != null) {
      // 最大のユーザーIDに1を加える
      return PREFIX_USER_ID + String.format("%04d", Integer.parseInt(maxUserId.substring(1)) + 1);
    } else {
      // ユーザーIDが1件も存在しない場合、開始番号を返す
      return PREFIX_USER_ID + START_NO;
    }
  }

  public String getUserName(String userId) {
    return userRepository.selectUserName(userId);
  }
}
