package com.aitrades.blockchain.eth.gateway.repository;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.aitrades.blockchain.eth.gateway.domain.ApproveTransaction;
import com.mongodb.client.result.DeleteResult;

@Repository
public class ApproveTransactionRepository {

	private static final String ID = "id";
	
	@Resource(name = "approveTransactionMongoTemplate")
	public ReactiveMongoTemplate approveTransactionMongoTemplate;

	public ApproveTransaction insert(ApproveTransaction approveTransaction) {
		return approveTransactionMongoTemplate.insert(approveTransaction).block();
	}

	public ApproveTransaction find(String id) {
		return approveTransactionMongoTemplate.findById(id, ApproveTransaction.class).block();
	}

	public void update(ApproveTransaction approveTransaction) {
		approveTransactionMongoTemplate.save(approveTransaction).block();
	}

	public void delete(ApproveTransaction approveTransaction) {
		Query query = new Query();
        query.addCriteria(Criteria.where(ID).is(approveTransaction.getId()));
        DeleteResult deleteResult = approveTransactionMongoTemplate.remove(approveTransaction).block();
        if(deleteResult != null && deleteResult.getDeletedCount() == 0) {
        	System.out.println("not deleted");
		}else {
			System.out.println("deleted");
		}
	}

}
