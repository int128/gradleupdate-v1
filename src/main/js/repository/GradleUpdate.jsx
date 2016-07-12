import qwest from "qwest";

export default class {
  constructor(token) {
    this._token = token;
  }
  update(fullName, gradleVersion) {
    return qwest.post(`/${fullName}/update`, {
      gradle_version: gradleVersion
    }, {
      headers: {Authorization: `token ${this._token}`}
    });
  }
}
