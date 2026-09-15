package com.swack.hcs.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.swack.hcs.service.LoginService;

/**
 * ログイン認証を行うためのインターセプタークラス.
 *
 * @author 情報太郎
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

  @Autowired
  @Lazy
  private LoginService loginService;

  /**
   * ログイン認証を行うためのメソッド.
   *
   * ログイン情報が存在しない場合はログイン画面にリダイレクトします。
   * ログイン情報が存在する場合はそのまま処理を続行します。
   *
   * @param request  リクエスト情報
   * @param response レスポンス情報
   * @param handler  ハンドラ情報
   * @return ログイン情報が存在しない場合はfalse、存在する場合はtrue
   */
  @SuppressWarnings("null")
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

    // ログイン情報が存在しない場合はログイン画面にリダイレクト
    if (!loginService.isLogin()) {
      // ログイン画面にリダイレクト
      response.sendRedirect("/login");
      return false;
    }

    // ログイン情報が存在する場合はそのまま処理を続行
    return true;
  }
}
