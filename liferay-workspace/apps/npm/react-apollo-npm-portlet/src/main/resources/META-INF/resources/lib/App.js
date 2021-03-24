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
              <h1 className="text-center mb-4">
                {Liferay.Language.get("users")}
              </h1>
              <UserCardList />
            </React.Fragment>
          ) : (
            <ClayAlert displayType="warning" title="Attention:">
              {Liferay.Language.get("you-have-attempted-to-access-a-section-of-the-site-that-requires-authentication")}
            </ClayAlert>
          )}
        </div>
      </div>
    </ApolloProvider>
  );
}

function createApolloClient() {
  return new ApolloClient({
    uri: `/o/graphql?p_auth=${Liferay.authToken}`,
    credentials: 'same-origin'
  });
}

export function isSignedIn() {
  return Liferay.ThemeDisplay.isSignedIn();
}
