import React from 'react';
import UserCard from './UserCard';
import { useQuery } from '@apollo/react-hooks';
import { gql } from 'apollo-boost';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayAlert from '@clayui/alert';
import { Liferay } from '../App';

export default function UserCardList() {

let [page, setPage] = React.useState(1);

  const ALL_USERS = gql`
  query{
    userAccounts(page: ${page}) {
      items {
        name,
        alternateName,
        id
      }
      page
      pageSize
      totalCount
    }
  }
  `;

  const { loading, error, data } = useQuery(ALL_USERS);

  if (loading) return <ClayLoadingIndicator />;
  if (error) {
    return (
      <ClayAlert displayType="danger" title="Error:">
        An error occured while loading data.
        <div className="mt-2">
          <code>{error.message}</code>
        </div>
      </ClayAlert>
    );
  }

  if (data.userAccounts.items.length === 0) {
    return (
      <ClayAlert displayType="info" className="text-center">
          {Liferay().Language.get("this-organization-does-not-have-any-users")}
      </ClayAlert>
    );
  }

  const cards = data.userAccounts.items.map(
    ({name, id, alternateName}) => (
      <UserCard
        key={id}
        name={name}
        alternateName={alternateName}
      />
    )
  );

  return <div className="row">{cards}</div>;
}
