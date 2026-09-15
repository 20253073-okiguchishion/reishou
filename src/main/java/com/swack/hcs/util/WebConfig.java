package com.swack.hcs.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * システム全体の設定を行うための管理クラスです。
 *
 * DIの設定やシステム環境設定、システム全体に関わる定数を
 * 設定するために利用し、その他の設定に関しては
 * application.propertiesファイルに記述します。
 *
 * @author 情報太郎
 *
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Autowired
  private LoginInterceptor loginInterceptor;

  /**
   * 認可処理用のインターセプターを登録.
   *
   * @param registry インターセプター登録用のレジストリ
   */
  @SuppressWarnings("null")
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(loginInterceptor).addPathPatterns("/**")
        // 認可処理不要なURLパスを指定
        .excludePathPatterns(
            "/login", // ログイン処理
            "/logout", // ログアウト処理
            "/signup", // 新規登録処理
            "/css/**", // CSSファイル
            "/js/**", // JavaScriptファイル
            "/images/**" // 画像ファイル
        );
  }

  /**
   * パスワードのエンコードに使用するPasswordEncoderを提供.
   *
   * @return PasswordEncoderオブジェクト
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
