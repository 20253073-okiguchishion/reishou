document.addEventListener("DOMContentLoaded", () => {
  const elHiddenRoomList = document.getElementById("hiddenRoomId");
  const elSendInput = document.getElementById("send");
  elSendInput.value = elHiddenRoomList.getAttribute("value");

});

$(function () {
  const $sendButton = $("#send");
  const $userList = $("select");

  // multipleSelectの初期化とイベントハンドリング
  $userList.multipleSelect({
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
    const selectedValues = $userList.multipleSelect("getSelects");

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
