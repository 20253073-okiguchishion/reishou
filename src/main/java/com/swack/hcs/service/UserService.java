package com.swack.hcs.service;

import com.swack.hcs.bean.UserData;
import com.swack.hcs.repository.UserRepository;
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

}
