/**
 * www.TheAIGames.com 
 * Heads Up Omaha pokerbot
 *
 * Last update: May 07, 2014
 *
 * @author Jim van Eeden, Starapple
 * @version 1.0
 * @License MIT License (http://opensource.org/Licenses/MIT)
 */


package bot;

import poker.Card;
import poker.HandHoldem;
import poker.PokerMove;

import com.stevebrecher.HandEval;
import static java.lang.Math.abs;
import java.util.Arrays;
import java.util.List;

/**
 * This class is the brains of your bot. Make your calculations here and return the best move with GetMove
 */
public class BotStarter implements Bot {

	/**
	 * Implement this method to return the best move you can. Currently it will return a raise the ordinal value
	 * of one of our cards is higher than 9, a call when one of the cards has a higher ordinal value than 5 and
	 * a check otherwise.
	 * @param state : The current state of your bot, with all the (parsed) information given by the engine
	 * @param timeOut : The time you have to return a move
	 * @return PokerMove : The move you will be doing
	 */
	@Override
	public PokerMove getMove(BotState state, Long timeOut) {
		HandHoldem hand = state.getHand();
		String handCategory = getHandCategory(hand, state.getTable()).toString();
		System.err.printf("my hand is %s, opponent action is %s, pot: %d\n", handCategory, state.getOpponentAction(), state.getPot());
		int num_of_cards_on_table = state.getTable().length;
                
		// Get the ordinal values of the cards in your hand
		int height1 = hand.getCard(0).getHeight().ordinal();
		int height2 = hand.getCard(1).getHeight().ordinal();
                int suit1 = hand.getCard(0).getSuit().ordinal();
		int suit2 = hand.getCard(1).getSuit().ordinal();
                
                List great_hand = Arrays.asList("FULL_HOUSE", "FOUR_OF_A_KIND",
                        "STRAIGHT_FLUSH","STRAIGHT", "FLUSH");
                
                List good_hand = Arrays.asList("TWO_PAIR", "THREE_OF_A_KIND", "PAIR");
                
                //getHandCategory(HandHoldem hand, Card[] table);
                PokerMove raise = new PokerMove(state.getMyName(), "raise", 2*state.getBigBlind());
		PokerMove call = new PokerMove(state.getMyName(), "call", state.getAmountToCall());
                PokerMove check = new PokerMove(state.getMyName(), "check", 0);
		// Return the appropriate move according to our amazing strategy
                // Logic for pre-flop
                if(num_of_cards_on_table == 0) {
                    if (handCategory.equals("PAIR") ) {
                        System.err.println("PAIR!!!");
                        if (height1 > 9) {
                            return raise;
                        } else if (height1 > 4 ) {
                            return call;
                        } else {
                            return check;
                        }                        
                    } else if ( suit1 == suit2 ) {
                        System.err.println("Suits are " + suit1 + " annnnd " + suit2);
                        System.err.println("POSSIBLE FLUSH!!");
                        if (height1 > 9 || height2 > 9) {
                            return raise;
                        } else if (abs(height1 - height2) < 5) {
                            return call;
                        } else {
                            return check;
                        }
                    } else if (abs(height1 - height2) < 5) {
                        System.err.println("POSSIBLE STRAIT!");
                        if (height1 > 9 && height2 > 9) {
                            return raise;
                        } else if (height1 > 6 && height2 > 6 ) {
                            return call;
                        } else {
                            return check;
                        }     
                    } else if (height1 > 10 || height2 > 10) {
                        return call;
                    } else {
                        System.err.println("Meh deal...");
                        return check;
                    }
                    
                } else if ( great_hand.contains(handCategory) ) {
                    System.err.println("GREAT HAND!!!");
                    return raise;
                } else if ( good_hand.contains(handCategory) ) {
                    System.err.println("GOOD HAND!!");
                    return call;
                } else {
                    System.err.println("Meh hand...");
                    return check;
                }
                        
                        
                
                
                
                // SECOND TRY AT LOGIC
                /*        
                        && (height1 > 10 || height2 > 10)   ) {
			return call_it;
                } else if ( height1 > 9
                            && num_of_cards_on_table == 0
                            && handCategory.equals("PAIR") ) {
			return raise_it;
                } else if ( num_of_cards_on_table == 0
                            && suit1 == suit2 ) {
			return call_it;
                } else if( good_hand.contains(handCategory) ) {
			return raise_it;
		} else if ( height1 > 8 && height2 > 8 ) {
                        return call_it;
                } else {
			return check_it;        
                }
                */
                // FIRST TRY AT LOGIC
                /*
                if( num_of_cards_on_table == 0
                    && handCategory.equals("PAIR")
                    && height1 > 9 && height2 > 9   ) {
			return new PokerMove(state.getMyName(), "raise", 2*state.getBigBlind());
		} else if( num_of_cards_on_table == 0
                           && abs( height1 - height2) < 5 
                           && suit1 == suit2 )  {
			return new PokerMove(state.getMyName(), "raise", 2*state.getBigBlind());
		} else if( num_of_cards_on_table == 0
                           && suit1 == suit2 
                           && height1 > 5 && height2 > 5 && suit1 == suit2) {
			return new PokerMove(state.getMyName(), "call", state.getAmountToCall());
		} else if( good_hand.contains(handCategory) ) {
			return new PokerMove(state.getMyName(), "raise", state.getAmountToCall());
		} else if( handCategory.equals("PAIR") ) {
			return new PokerMove(state.getMyName(), "call", state.getAmountToCall());
		} else {
			return new PokerMove(state.getMyName(), "check", 0);
		}
                */
            //return new PokerMove(state.getMyName(), "raise", 2*state.getBigBlind());
	}
	
	/**
	 * Calculates the bot's hand strength, with 0, 3, 4 or 5 cards on the table.
	 * This uses the com.stevebrecher package to get hand strength.
	 * @param hand : cards in hand
	 * @param table : cards on table
	 * @return HandCategory with what the bot has got, given the table and hand
	 */
	public HandEval.HandCategory getHandCategory(HandHoldem hand, Card[] table) {
		if( table == null || table.length == 0 ) { // there are no cards on the table
			return hand.getCard(0).getHeight() == hand.getCard(1).getHeight() // return a pair if our hand cards are the same
					? HandEval.HandCategory.PAIR
					: HandEval.HandCategory.NO_PAIR;
		}
		long handCode = hand.getCard(0).getNumber() + hand.getCard(1).getNumber();
		
		for( Card card : table ) { handCode += card.getNumber(); }
		
		if( table.length == 3 ) { // three cards on the table
			return rankToCategory(HandEval.hand5Eval(handCode));
		}
		if( table.length == 4 ) { // four cards on the table
			return rankToCategory(HandEval.hand6Eval(handCode));
		}
		return rankToCategory(HandEval.hand7Eval(handCode)); // five cards on the table
	}
	
	/**
	 * small method to convert the int 'rank' to a readable enum called HandCategory
         * @param rank
         * @return 
	 */
	public HandEval.HandCategory rankToCategory(int rank) {
		return HandEval.HandCategory.values()[rank >> HandEval.VALUE_SHIFT];
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BotParser parser = new BotParser(new BotStarter());
		parser.run();
	}

}
