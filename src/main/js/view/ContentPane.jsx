import React from "react";

export default class extends React.Component {
  render() {
    return (
      <section>
        <table className="table">
          <tbody>
            {this.props.repos ? this.props.repos.map((repo) => (
              <tr>
                <td>
                  <img src={`/${repo.full_name}/status.svg`}/>
                </td>
                <td>
                  {repo.owner.login}/{repo.name}
                  <br/>
                  {repo.description}
                </td>
              </tr>
            )) : null}
          </tbody>
        </table>
      </section>
    );
  }
}
