"use strict";
// document.addEventListener("DOMContentLoaded", () => {
//   // HTML要素を変数に代入
//   const elSendButton = document.getElementById("send");
//   const elRoomList = document.getElementById("roomList");

//   //チェックボックスリストの状態が変化したときの処理
//   elRoomList.addEventListener("input", (e) => {
//     if (e.target.checked) {
//       elSendButton.classList.remove("btn-secondary");
//       elSendButton.classList.add("btn-primary");
//       elSendButton.disabled = false;
//     } else {
//       elSendButton.classList.remove("btn-primary");
//       elSendButton.classList.add("btn-secondary");
//       elSendButton.disabled = true;
//     }
//   });
// });

$(function () {
  const $sendButton = $("#send");
  const $roomList = $("select");

  // multipleSelectの初期化とイベントハンドリング
  $roomList.multipleSelect({
    width: 200,
    // 「すべて」チェックボックスの表示
    formatSelectAll: function () {
      return "すべて";
    },
    // すべて選択されている場合の表示
    formatAllSelected: function () {
      return "全て選択されています";
    },
    // 個別のチェックボックスがクリックされたとき
    onClick: function () {
      updateButtonState();
    },
    // 「すべて」チェックボックスがクリックされたとき
    onCheckAll: function () {
      updateButtonState();
    },
    onUncheckAll: function () {
      updateButtonState();
    },
  });

  // ボタンの活性・非活性
  function updateButtonState() {
    // 選択されている値の配列を取得
    const selectedValues = $roomList.multipleSelect("getSelects");

    // 1つ以上選択されている場合
    if (selectedValues.length > 0) {
      $sendButton.removeClass("btn-secondary").addClass("btn-primary");
      $sendButton.prop("disabled", false);
    } else {
      // 何も選択されていない場合
      $sendButton.removeClass("btn-primary").addClass("btn-secondary");
      $sendButton.prop("disabled", true);
    }
  }
});

document.addEventListener("DOMContentLoaded", () => {
  const elSendInput = document.getElementById("send");
  const elRoomList = document.getElementById("RoomId");
  const elUserList = document.getElementById("userList");
  elSendInput.value = elRoomList.getAttribute("value");

  elUserList.addEventListener("change", () => {
    if (elUserList.value) {
      elSendInput.classList.remove("btn-secondary");
      elSendInput.classList.add("btn-primary");
      elSendInput.disabled = false;
    } else {
      elSendInput.classList.remove("btn-primary");
      elSendInput.classList.add("btn-secondary");
      elSendInput.disabled = true;
    }
  });
});
