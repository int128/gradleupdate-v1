import React from "react";
import GitHub from "../repository/GitHub.jsx";
import MenuPane from "./MenuPane.jsx";
import ContentPane from "./ContentPane.jsx";
import Footer from "./Footer.jsx";

export default class extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
  }
  componentDidMount() {
    const github = new GitHub(this.props.token);
    github.getUser()
      .then((xhr, user) => this.setState({user: user}));
    github.findRepositories({sort: 'updated'})
      .then((xhr, repos) => this.setState({repos: repos}));
  }
  render() {
    return (
      <div className="container">
        <div className="row">
          <div className="col-lg-3 col-md-3 col-sm-3">
            <MenuPane
              user={this.state.user}
              onSignOut={this.props.onUnauthorize.bind(this)}/>
          </div>
          <div className="col-lg-9 col-md-9 col-sm-9">
            <ContentPane
              repos={this.state.repos}/>
          </div>
        </div>
        <Footer/>
      </div>
    );
  }
}
