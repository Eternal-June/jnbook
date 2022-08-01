function isValidPhone(str) {
  var myreg = /^[1][3,4,5,7,8,9][0-9]{9}$/;

  if (!myreg.test(str)) {
    return false;
  } else {
    return true;
  }
}

function isValidEmail(str) {
  var myreg = /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;

  if (!myreg.test(str)) {
    return false;
  } else {
    return true;
  }
}

module.exports = {
  isValidPhone,
  isValidEmail
};