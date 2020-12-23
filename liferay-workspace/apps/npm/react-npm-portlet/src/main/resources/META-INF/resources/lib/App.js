import React from 'react';
import UserCardList from './components/UserCardList';
import ApolloClient from 'apollo-boost';
import { ApolloProvider } from '@apollo/react-hooks';
import ClayAlert from '@clayui/alert';

export default function App() {
  return (
    <ApolloProvider client={createApolloClient()}>
      <div className="react-portlet">
        <div className="container">
          {isSignedIn() ? (
            <React.Fragment>
              <h1 className="text-center mb-4">Users cards</h1>
              <UserCardList />
            </React.Fragment>
          ) : (
            <ClayAlert displayType="warning" title="Attention:">
              You need to sign in to see this content.
            </ClayAlert>
          )}
        </div>
      </div>
    </ApolloProvider>
  );
}

function createApolloClient() {
  let endpoint;
  // outside Liferay Portal
  if (process.env.NODE_ENV === 'development') {
    const user = process.env.REACT_APP_LIFERAY_USER;
    const password = process.env.REACT_APP_LIFERAY_PASSWORD;
    const base64credentials = new Buffer(`${user}:${password}`).toString(
      'base64'
    );

    endpoint =
      process.env.REACT_APP_LIFERAY_HOST +
      process.env.REACT_APP_LIFERAY_GRAPHQL_ENDPOINT;

    return new ApolloClient({
      uri: endpoint,
      headers: {
        Authorization: 'Basic ' + base64credentials
      }
    });
  }

  endpoint = `${'/o/graphql'}?p_auth=${
    Liferay().authToken
  }`;

  return new ApolloClient({
    uri: endpoint,
    credentials: 'same-origin'
  });
}

export function isSignedIn() {
  if (process.env.NODE_ENV === 'development') {
    return true;
  }
  return Liferay().ThemeDisplay.isSignedIn();
}

export function Liferay() {
  return window['Liferay'];
}
